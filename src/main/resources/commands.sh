./mongod --dbpath ../CacheDb

sudo systemctl start redis
sudo systemctl stop redis

./connect-standalone.sh ../config/connect-standalone.properties ../config/mongo-source.properties
./kafka-topics.sh --bootstrap-server=localhost:9092 --list
./kafka-topics.sh --bootstrap-server localhost:9092 --topic mongodb.cacheDb.Correlation --create
./kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic mongodb.cacheDb.Correlation