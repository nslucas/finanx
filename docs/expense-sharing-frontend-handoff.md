# Expense Sharing Frontend Handoff

This document describes the backend contract for connecting users, creating shared expenses, and rendering the `Acertos` screen.

## Flow Overview

1. Maria opens her connection area and copies her code from `GET /connections/code`.
2. Lucas shares his code with Maria.
3. Maria sends `POST /connections/requests` with Lucas' code.
4. The backend creates a `PENDING` connection request.
5. When Lucas opens the app, the frontend calls `GET /connections/requests/pending`.
6. Lucas sees a prompt such as `Maria wants to connect with you to share expenses.`
7. Lucas accepts or declines the request.
8. After an `ACCEPTED` connection exists, either user can create a shared expense.
9. `GET /settlements` powers the `Acertos` summary, for example `Lucas owes R$ 40.00 to Maria`.

All endpoints below require `Authorization: Bearer <jwt>`.

## Enums

- `ConnectionStatus`: `PENDING`, `ACCEPTED`, `DECLINED`
- `ExpenseShareStatus`: `OPEN`, `SETTLED`
- `SettlementDirection`: `YOU_OWE`, `OWES_YOU`

## Connections

### `GET /connections/code`

Returns the authenticated user's public connection code.

Response:

```json
{
  "code": "ABC12345"
}
```

### `POST /connections/requests`

Creates a pending connection request from the authenticated user to the user owning `targetCode`.

Request:

```json
{
  "targetCode": "ABC12345"
}
```

Response: `201 Created`

```json
{
  "id": 10,
  "requesterUserId": 1,
  "requesterName": "Maria Silva",
  "targetUserId": 2,
  "targetName": "Lucas Nunes",
  "status": "PENDING",
  "requestedAt": "2026-06-23T10:00:00",
  "respondedAt": null
}
```

### `GET /connections/requests/pending`

Lists pending requests where the authenticated user is the target.

Response:

```json
[
  {
    "id": 10,
    "requesterUserId": 1,
    "requesterName": "Maria Silva",
    "targetUserId": 2,
    "targetName": "Lucas Nunes",
    "status": "PENDING",
    "requestedAt": "2026-06-23T10:00:00",
    "respondedAt": null
  }
]
```

### `POST /connections/requests/{id}/accept`

Accepts a pending request. Only the target user can accept it.

Response:

```json
{
  "id": 10,
  "requesterUserId": 1,
  "requesterName": "Maria Silva",
  "targetUserId": 2,
  "targetName": "Lucas Nunes",
  "status": "ACCEPTED",
  "requestedAt": "2026-06-23T10:00:00",
  "respondedAt": "2026-06-23T10:05:00"
}
```

### `POST /connections/requests/{id}/decline`

Declines a pending request. Only the target user can decline it.

Response shape is the same as accept, with `status: "DECLINED"`.

### `GET /connections`

Lists accepted connections for the authenticated user.

Response:

```json
[
  {
    "id": 10,
    "requesterUserId": 1,
    "requesterName": "Maria Silva",
    "targetUserId": 2,
    "targetName": "Lucas Nunes",
    "status": "ACCEPTED",
    "requestedAt": "2026-06-23T10:00:00",
    "respondedAt": "2026-06-23T10:05:00"
  }
]
```

## Shared Expenses

Use the existing `POST /expenses` and `PUT /expenses/{expenseId}` endpoints. The `share` object is optional.

Create request:

```json
{
  "name": "Mercado",
  "amount": 55,
  "installmentCount": 1,
  "purchaseDate": "2026-06-23T10:00:00",
  "description": "Compra dividida",
  "cardId": 1,
  "categoryId": 4,
  "share": {
    "participantUserId": 2,
    "creatorAmount": 15,
    "participantAmount": 40
  }
}
```

Response remains the normal expense shape. The share is persisted for `Acertos`; the participant does not receive an automatic transaction, expense, or card statement entry.

Rules:

- `participantUserId` must be a user with an `ACCEPTED` connection to the authenticated user.
- `participantUserId` cannot be the authenticated user.
- `creatorAmount` must be zero or greater.
- `participantAmount` must be greater than zero.
- `creatorAmount + participantAmount` must equal `amount`.
- Updating a shared expense updates or removes only an `OPEN` share.
- Updating or deleting a shared expense with a `SETTLED` share returns `400`.

## Settlements

### `GET /settlements`

Returns net open balances by connected person.

Response:

```json
[
  {
    "counterpartyUserId": 2,
    "counterpartyName": "Lucas Nunes",
    "amount": 40,
    "direction": "OWES_YOU"
  },
  {
    "counterpartyUserId": 3,
    "counterpartyName": "Ana Costa",
    "amount": 25,
    "direction": "YOU_OWE"
  }
]
```

Frontend copy guidance:

- `OWES_YOU`: `{counterpartyName} owes R$ {amount} to you`
- `YOU_OWE`: `You owe R$ {amount} to {counterpartyName}`

### `GET /settlements/items?counterpartyUserId=`

Lists shared expense items for the authenticated user. `counterpartyUserId` is optional.

Response:

```json
[
  {
    "shareId": 8,
    "expenseId": 12,
    "expenseName": "Mercado",
    "expenseAmount": 55,
    "creatorUserId": 1,
    "creatorName": "Maria Silva",
    "participantUserId": 2,
    "participantName": "Lucas Nunes",
    "participantAmount": 40,
    "direction": "OWES_YOU",
    "status": "OPEN",
    "createdAt": "2026-06-23T10:00:00",
    "settledAt": null
  }
]
```

### `POST /settlements/items/{shareId}/settle`

Marks an open shared expense as settled. This does not create a bank transaction, card payment, or account movement.

Response:

```json
{
  "shareId": 8,
  "expenseId": 12,
  "expenseName": "Mercado",
  "expenseAmount": 55,
  "creatorUserId": 1,
  "creatorName": "Maria Silva",
  "participantUserId": 2,
  "participantName": "Lucas Nunes",
  "participantAmount": 40,
  "direction": "OWES_YOU",
  "status": "SETTLED",
  "createdAt": "2026-06-23T10:00:00",
  "settledAt": "2026-06-23T10:15:00"
}
```

## Frontend UX Notes

- On app boot, call `GET /connections/requests/pending` after authentication.
- Show each pending request as an actionable notification or modal.
- Use `GET /connections` to populate the participant selector in the shared expense form.
- In the expense form, keep `amount` as the total paid by the creator and send the split in `share`.
- The `Acertos` tab should use `GET /settlements` for the summary list and `GET /settlements/items?counterpartyUserId=` for detail rows.
- Settling an item should invalidate `settlements`, `settlement items`, and any local badges/counts, but it does not need to invalidate accounts, cards, statements, budgets, or summaries.

## Expected Errors

Standard errors follow the existing API shape with `status`, `error`, `message`, and `path`.

- Invalid or unknown code: `404`, `Connection code not found`.
- Missing code: `400`, `Target code is required`.
- Self connection: `400`, `Cannot connect with yourself`.
- Duplicate pending or accepted connection: `400`, `A pending or accepted connection already exists`.
- Accepting/declining as the wrong user: `400`, `Connection request cannot be answered by this user`.
- Sharing without accepted connection: `400`, `Users must have an accepted connection to share expenses`.
- Invalid split sum: `400`, `Shared amounts must add up to the expense amount`.
- Settling an already settled item: `400`, `Settlement item is already settled`.
- Updating/deleting a settled shared expense: `400`, `Settled shared expenses cannot be updated` or `Settled shared expenses cannot be deleted`.
