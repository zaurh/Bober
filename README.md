[![](https://raw.githubusercontent.com/helloworlditsm/APK/main/github_cover2.svg)](https://play.google.com/store/apps/details?id=com.zaurh.bober)

![Logo](https://i.hizliresim.com/732zu2s.jpg)



## Bober is a dating application where you can meet new people and chat with them in real time.


## ⬇*** Android Side ***⬇

Kotlin

Jetpack compose

MVVM

Clean Architecture

Dagger Hilt (injecting dependencies)

Navigation (navigating between composables)

Retrofit (

@GET: retrieving messages, user data, etc.

@PUT: Like, Unlike, Block, Unblock, etc. 

@POST: Sign In, Sign Up, Checking username, etc.

@DELETE: Deletion of user data.

)

OkHttp (Connecting WebSockets)

Preferences data store (controlling notifications, enabling dark/light mode, saving JWT token)

Foreground services (Keeping web sockets open in the background and getting notifications.)

Coil (loading images and GIF asynchronously)

Splash Screen API (showing splash screen during authentication)



## ⬇*** Back-end Side (KTOR) ***⬇

JWT Authentication (authenticating users)

Routing (sending requests and getting responses) 

Koin (injecting dependencies)

WebSockets (real-time communication among users): typing, online/offline, last seen, message was read/delivered, etc. 

Mongo DB (storing user and message data) 

Firebase Storage (compress images and storing them in much suitable size)

Security: Hashing and salting (When users set a password, it is being hashed and salted before storing in the database.) 

Encryption (When users allow their locations, they are encrypted before storing in the database.)

Decryption (For safety reasons, the location decryption process is going on the server side.)

## Third-party libraries

 - [Wheel Picker Compose](https://github.com/commandiron/WheelPickerCompose)
 - [LazyScrollBar](https://github.com/nanihadesuka/LazyColumnScrollbar)
 - [Lottie](https://lottiefiles.com/)
 - [Swipeable Card](https://github.com/alexstyl/compose-tinder-card)


## Features

- Light/dark mode toggle
- In-app messaging
- Block/unblock
- Like/unlike
- Delete user data
- Report user
- Share profile
- Manage notifications


## API Reference

#### Get all messages or users

```http
  GET /BASE_URL/messages
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `token` | `string` | **Required**. JWT Token |

#### Update user data

```http
  PUT /BASE_URL/update_user
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `token`      | `string` | **Required**. JWT Token |

#### Report user
```http
  POST /BASE_URL/report/{recipientId}/{reason}/{optional}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `token`      | `string` | **Required**. JWT Token |

#### Delete user data
```http
  DELETE /BASE_URL/delete_user_data
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `token`      | `string` | **Required**. JWT Token |


## License

[MIT](https://raw.githubusercontent.com/zaurh/Bober/master/LICENSE?token=GHSAT0AAAAAACQZPWCC6T42U43HV75PHU6GZW2YYJQ)


## Feedback

If you have any feedback, please reach out to me at zaurway@gmail.com

