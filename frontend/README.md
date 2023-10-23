# TP1

Application de messagerie simple.

L'énoncé du TP est disponible [ici](https://github.com/coderunner/INF5190/blob/main/tp/tp1/tp1-angular.pdf).


To test this app 

```curl
curl --location 'localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: sid=cd49b114-0d7a-4475-a65c-21eafff20078' \
--data '{
    "username": "tarik",
    "password": "123"
}'


```