# User Preferences Frontend Handoff

This document describes the backend contract for user-editable preferences. The first version supports movement-entry defaults for the authenticated user.

## Overview

- Preferences are owned by the authenticated user from the JWT.
- Do not send `userId`.
- The backend persists preferences so they survive browser changes, cache clearing, and new devices.
- If a user has never saved preferences, the API returns safe defaults.
- If a saved default points to an inactive or missing account, card, or category, the backend returns `null` for that default.

## Enums

`MovementKind`:

- `INCOME`
- `EXPENSE`
- `CARD_EXPENSE`
- `ADJUSTMENT`
- `TRANSFER`
- `CARD_PAYMENT`

## Preference Shape

```json
{
  "defaultMovementKind": "CARD_EXPENSE",
  "defaultAccountId": null,
  "defaultTargetAccountId": null,
  "defaultCardId": null,
  "defaultExpenseCategoryId": null,
  "defaultIncomeCategoryId": null,
  "defaultInstallmentCount": 1
}
```

Field meanings:

- `defaultMovementKind`: initial type for a new movement.
- `defaultAccountId`: default account for income, account expense, adjustment, card payment source account, or transfer source.
- `defaultTargetAccountId`: default transfer destination account.
- `defaultCardId`: default card for card purchases and card payments.
- `defaultExpenseCategoryId`: default category for account expenses and card purchases.
- `defaultIncomeCategoryId`: default category for income.
- `defaultInstallmentCount`: default installment count for card purchases.

## Endpoints

### `GET /me/preferences`

Returns the authenticated user's preferences.

Response when the user has no saved preferences:

```json
{
  "defaultMovementKind": "CARD_EXPENSE",
  "defaultAccountId": null,
  "defaultTargetAccountId": null,
  "defaultCardId": null,
  "defaultExpenseCategoryId": null,
  "defaultIncomeCategoryId": null,
  "defaultInstallmentCount": 1
}
```

### `PUT /me/preferences`

Creates or replaces the authenticated user's preferences.

Request:

```json
{
  "defaultMovementKind": "EXPENSE",
  "defaultAccountId": 1,
  "defaultTargetAccountId": null,
  "defaultCardId": 2,
  "defaultExpenseCategoryId": 3,
  "defaultIncomeCategoryId": 4,
  "defaultInstallmentCount": 1
}
```

Response:

```json
{
  "defaultMovementKind": "EXPENSE",
  "defaultAccountId": 1,
  "defaultTargetAccountId": null,
  "defaultCardId": 2,
  "defaultExpenseCategoryId": 3,
  "defaultIncomeCategoryId": 4,
  "defaultInstallmentCount": 1
}
```

Particularities:

- Sending `null` clears an optional default.
- If `defaultMovementKind` is `null`, the backend stores `CARD_EXPENSE`.
- If `defaultInstallmentCount` is `null`, the backend stores `1`.
- `defaultInstallmentCount` must be greater than zero.

## Validation

The backend validates all referenced resources:

- Account defaults must belong to the authenticated user and be active.
- Card defaults must belong to the authenticated user and be active.
- `defaultExpenseCategoryId` must belong to the authenticated user, be active, and have type `EXPENSE`.
- `defaultIncomeCategoryId` must belong to the authenticated user, be active, and have type `INCOME`.

Expected errors:

- Missing or other-user resource: `404` with a standard error body.
- Inactive resource, wrong category type, or invalid installment count: `400` with a standard error body.

## Frontend Guidance

Settings screen:

- Use `GET /me/preferences` to populate the preference form.
- Populate selectors from existing active resources:
  - `GET /accounts`
  - `GET /cards`
  - `GET /categories`
- Use `PUT /me/preferences` to save the full preference shape.
- Treat `null` values as "no default".

Movements screen:

- Load preferences before opening a new movement form when possible.
- If preferences are still loading, keep the existing fallback: `CARD_EXPENSE` and `defaultInstallmentCount = 1`.
- Opening a new movement should use `defaultMovementKind`.
- Switching the movement type should apply the relevant defaults:
  - `EXPENSE`: `defaultAccountId`, `defaultExpenseCategoryId`.
  - `CARD_EXPENSE`: `defaultCardId`, `defaultExpenseCategoryId`, `defaultInstallmentCount`.
  - `INCOME`: `defaultAccountId`, `defaultIncomeCategoryId`.
  - `TRANSFER`: `defaultAccountId`, `defaultTargetAccountId`.
  - `CARD_PAYMENT`: `defaultCardId`, `defaultAccountId`.
  - `ADJUSTMENT`: `defaultAccountId`.
- Editing an existing movement should keep using the movement's own values and must not be overwritten by preferences.

## Cleanup Behavior

When an account, card, or category is deactivated, the backend clears saved preferences pointing to that resource. This prevents the frontend from opening new movement forms with stale default IDs.
