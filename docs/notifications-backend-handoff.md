# Notifications Backend Handoff

This document describes the backend work needed to support Prospera Notifications V1. The frontend already calls these contracts and treats notification/push failures as non-fatal so missing endpoints do not log users out.

## Goals

Support both:

- In-app notifications persisted per user.
- Browser push notifications through standard Web Push with VAPID.

V1 notification events:

- Incoming connection request: immediate in-app + push.
- New shared expense where the authenticated user is the participant: immediate in-app + push.
- Financial alerts from `GET /alerts`: daily digest in-app + push.

Existing screens remain the source of detail:

- Connection request opens `/connections`.
- Shared expense opens `/settlements`.
- Financial digest opens `/alerts`.

## Notification Types

Use these stable enum values because the frontend type map depends on them:

```text
NotificationCategory:
- CONNECTION_REQUEST
- SHARED_EXPENSE
- FINANCIAL_DIGEST

NotificationType:
- CONNECTION_REQUEST_RECEIVED
- SHARED_EXPENSE_RECEIVED
- FINANCIAL_ALERT_DIGEST
```

Suggested persisted notification fields:

```json
{
  "id": 123,
  "userId": 2,
  "type": "CONNECTION_REQUEST_RECEIVED",
  "category": "CONNECTION_REQUEST",
  "title": "Nova solicitação de conexão",
  "body": "Maria Silva quer se conectar com você.",
  "url": "/connections",
  "resourceType": "CONNECTION_REQUEST",
  "resourceId": 10,
  "readAt": null,
  "createdAt": "2026-06-27T15:30:00"
}
```

Frontend response shape excludes `userId`:

```json
{
  "id": 123,
  "type": "CONNECTION_REQUEST_RECEIVED",
  "category": "CONNECTION_REQUEST",
  "title": "Nova solicitação de conexão",
  "body": "Maria Silva quer se conectar com você.",
  "url": "/connections",
  "resourceType": "CONNECTION_REQUEST",
  "resourceId": 10,
  "readAt": null,
  "createdAt": "2026-06-27T15:30:00"
}
```

## REST API Contract

All endpoints require `Authorization: Bearer <jwt>`.

### `GET /notifications?limit=&unreadOnly=`

Returns authenticated user's notifications ordered newest first.

Parameters:

- `limit`: optional, default `50`, clamp to a reasonable max such as `100`.
- `unreadOnly`: optional boolean, default `false`.

Response:

```json
[
  {
    "id": 123,
    "type": "SHARED_EXPENSE_RECEIVED",
    "category": "SHARED_EXPENSE",
    "title": "Nova despesa compartilhada",
    "body": "Maria Silva compartilhou Mercado com você: R$ 40,00.",
    "url": "/settlements",
    "resourceType": "EXPENSE_SHARE",
    "resourceId": 8,
    "readAt": null,
    "createdAt": "2026-06-27T15:30:00"
  }
]
```

### `GET /notifications/unread-count`

Returns unread count for the authenticated user.

Response:

```json
{
  "count": 3
}
```

### `PATCH /notifications/{id}/read`

Marks one notification as read. Must only allow access to the owner.

Behavior:

- Idempotent: if already read, return it unchanged.
- Set `readAt` to current timestamp when previously unread.

Response: notification record.

### `POST /notifications/read-all`

Marks all authenticated user's unread notifications as read.

Recommended response: `204 No Content`.

## Web Push API Contract

Use standard Web Push with VAPID.

### `GET /push/vapid-public-key`

Returns the public VAPID key as URL-safe base64.

Response:

```json
{
  "publicKey": "BExamplePublicVapidKey..."
}
```

### `POST /push/subscriptions`

Upserts the browser subscription for the authenticated user.

Request body is the browser `PushSubscriptionJSON`:

```json
{
  "endpoint": "https://fcm.googleapis.com/fcm/send/...",
  "expirationTime": null,
  "keys": {
    "p256dh": "base64-key",
    "auth": "base64-auth-secret"
  }
}
```

Backend behavior:

- Use authenticated user from JWT; never accept `userId` in body.
- Upsert by `endpoint`.
- Store `endpoint`, `p256dh`, `auth`, optional `expirationTime`, `userId`, `createdAt`, `updatedAt`.
- Return `204 No Content` or `201 Created`.

### `POST /push/subscriptions/unsubscribe`

Removes one subscription for the authenticated user.

Request:

```json
{
  "endpoint": "https://fcm.googleapis.com/fcm/send/..."
}
```

Recommended response: `204 No Content`.

## Preferences Contract

Extend existing `GET /me/preferences` and `PUT /me/preferences` with:

```json
{
  "notifications": {
    "connectionRequests": true,
    "sharedExpenses": true,
    "financialDigest": true
  }
}
```

Defaults:

- All three fields default to `true`.
- Missing `notifications` object should be treated as all enabled for backward compatibility.
- `PUT /me/preferences` should preserve or update these booleans along with the existing movement defaults.

Backend must use these preferences for both in-app notification creation and push delivery. If a category is disabled, do not create a new in-app notification and do not send push for that category.

## Trigger Rules

### Incoming Connection Request

Trigger from `POST /connections/requests` after a pending request is successfully created.

Recipient:

- Target user only.

Notification:

```text
type: CONNECTION_REQUEST_RECEIVED
category: CONNECTION_REQUEST
title: Nova solicitação de conexão
body: {requesterName} quer se conectar com você.
url: /connections
resourceType: CONNECTION_REQUEST
resourceId: connectionRequest.id
```

Deduplication:

- Prevent duplicate unread notifications for the same `type`, `userId`, and `resourceId`.

### New Shared Expense

Trigger when `POST /expenses` creates a new expense with `share`.

Recipient:

- Participant user only.
- Do not notify the creator.

Notification:

```text
type: SHARED_EXPENSE_RECEIVED
category: SHARED_EXPENSE
title: Nova despesa compartilhada
body: {creatorName} compartilhou {expenseName} com você: R$ {participantAmount}.
url: /settlements
resourceType: EXPENSE_SHARE
resourceId: expenseShare.id
```

V1 does not require notifications for updates/removals to shared expenses.

### Daily Financial Digest

Backend should run once per day per user, ideally in the user's business timezone. The app currently uses `America/Sao_Paulo` elsewhere.

Source:

- Existing `AlertService` / `GET /alerts` logic.

Create a digest only when active alerts exist.

Notification:

```text
type: FINANCIAL_ALERT_DIGEST
category: FINANCIAL_DIGEST
title: Resumo financeiro do dia
body: Você tem {count} alerta(s), incluindo {criticalCount} crítico(s).
url: /alerts
resourceType: ALERT_DIGEST
resourceId: null
```

Deduplication:

- At most one `FINANCIAL_ALERT_DIGEST` per user per local date.
- Store a `digestDate` or use a unique key such as `FINANCIAL_ALERT_DIGEST:{userId}:{yyyy-mm-dd}`.

## Push Delivery

When a notification is created and the user's category preference is enabled:

1. Persist the in-app notification.
2. Find active push subscriptions for that user.
3. Send Web Push payload:

```json
{
  "title": "Nova despesa compartilhada",
  "body": "Maria Silva compartilhou Mercado com você: R$ 40,00.",
  "url": "/settlements",
  "tag": "SHARED_EXPENSE_RECEIVED:8",
  "notificationId": 123
}
```

Recommended behavior:

- If push send returns `404` or `410`, delete that subscription.
- Do not fail the original business transaction if push delivery fails after notification persistence.
- Log push failures with endpoint/user context, but avoid logging full auth secrets.

## Implementation Notes

Suggested Java/Spring additions:

- `Notification` entity + repository.
- `PushSubscription` entity + repository.
- `NotificationResource` for `/notifications`.
- `PushResource` for `/push`.
- `NotificationService` to centralize preference checks, persistence, dedupe, and push dispatch.
- `FinancialNotificationScheduler` for daily alert digest.
- Extend `UserPreference`, `UserPreferenceRecord`, and migrations for notification preference columns or an embedded JSON value.

Suggested DB constraints:

- `notifications.user_id` indexed.
- `notifications.read_at` indexed or included with `user_id` for unread count.
- Unique optional dedupe key for notification events.
- `push_subscriptions.endpoint` unique.

## Acceptance Checklist

- Login is unaffected if notification endpoints fail.
- `GET /notifications/unread-count` returns `{ "count": 0 }` for new users.
- Creating a connection request creates one target-user notification and, if subscribed, one push.
- Creating a shared expense creates one participant notification and no creator notification.
- Daily digest creates no notification when there are no alerts.
- Daily digest never creates more than one digest per user per local date.
- Disabled category preferences suppress both in-app notification creation and push.
- Notification click opens the `url` supplied in the push payload.
