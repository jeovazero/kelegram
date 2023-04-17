SERVER=http://localhost:8000
COOKIE=.cookie
basedir=$(dirname $0)
case $1 in
  newAccount)
    curl --cookie-jar .cookie -X POST -H "Content-type: application/json" -d @$basedir/data/newAccount.json -s $SERVER/account
    ;;
  me)
    curl --cookie .cookie  -X GET -H "Content-type: application/json" -s $SERVER/me
    ;;
  logout)
    curl --cookie .cookie  -X POST -H "Content-type: application/json" -sv $SERVER/logout
    ;;
esac
