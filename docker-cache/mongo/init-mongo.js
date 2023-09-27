db = db.getSiblingDB('volpaywritedb');

db.createUser(
{
user: "volpay",
pwd: "volpay",
roles: [ { role: "readWrite", db: "volpaywritedb" } ]
}
);
db.user.insert({name: "volpay"});
db = db.getSiblingDB('volpayreaddb');

db.createUser(
{
user: "volpay",
pwd: "volpay",
roles: [ { role: "readWrite", db: "volpayreaddb" } ]
}
);
db.user.insert({name: "volpay"});
