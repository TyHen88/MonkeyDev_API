# PayWay Service

Frontend-facing contract for PayWay checkout and verification.

## Base URL

`/api/wb/v1/payments/payway`

## Response Envelope

- `POST /purchase` returns raw HTML, not `ApiResponse`.
- `POST /return` and `POST /verify` return `ApiResponse<Object>`.

## Access Rules

- `POST /purchase` requires authentication.
- `POST /return` is public callback handling.
- `POST /verify` requires authentication.

## Request Models

### Purchase

`PaywayPurchaseRequest`

```json
{
  "req_time": "2026-04-10T10:30:00",
  "merchant_id": "merchant-id",
  "tran_id": "TXN-0001",
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "phone": "012345678",
  "type": "purchase",
  "payment_option": "abapay",
  "items_list": [
    {
      "name": "Wireless Earbuds",
      "quantity": 1,
      "price": 25.50
    }
  ],
  "shipping": 0,
  "amount": 25.50,
  "currency": "USD",
  "return_url": "https://frontend.example.com/payments/return",
  "cancel_url": "https://frontend.example.com/payments/cancel",
  "continue_success_url": "https://frontend.example.com/payments/success",
  "custom_fields": "{}",
  "return_params": "{}",
  "hash": "signature"
}
```

### Verify

`PaywayVerifyRequest`

```json
{
  "tran_id": "TXN-0001"
}
```

## Endpoints

### Create transaction

`POST /api/wb/v1/payments/payway/purchase`

Notes:

- The response is HTML checkout markup.
- Render it in a browser, iframe, or redirect flow depending on the frontend architecture.

### Return callback

`POST /api/wb/v1/payments/payway/return`

Notes:

- This is a backend callback endpoint.
- Frontend usually does not call it directly.

### Verify transaction

`POST /api/wb/v1/payments/payway/verify`

## Frontend Notes

- Use `purchase` to start checkout.
- Use `verify` after return or success redirect to confirm the transaction state.
- Do not assume payment success until verification succeeds.

