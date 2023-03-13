SERVER=http://localhost:8000
basedir=$(dirname $0)
case $1 in
  newAccount)
      curl -X POST -H "Content-type: application/json" -d @$basedir/data/newAccount.json $SERVER/account
    ;;
esac
