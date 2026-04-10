# OAuth2 Service

Browser redirect flow for Google OAuth2 login.

## Base URL

`/oauth2`

## Endpoints

### Start Google OAuth2

`GET /oauth2/authorization/google`

Notes:

- This redirects the browser to Google.
- Call it from a browser navigation, not from a JSON fetch call.

### Google callback

`GET /oauth2/callback/google`

### Legacy Google callback

`GET /oauth2/oauth2/callback/google`

## Frontend Notes

- Treat this as a redirect flow, not a JSON API.
- After the callback, the backend decides where to send the browser next.

