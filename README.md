# comp512-project
Project on distributed system.
## Structure
```
--Clinet
|
|-Middleware
    |-MwServer
    |-CarServer
    |-RoomServer
    |-FlightServer
    |-CustomerServer

```
## Build
```
cd Middleware/
make
cd Client/
make
```

## Instructions
To run the RMI resource managers and Middleware locally:

```
cd Middleware/
./run_roomserver.sh
./run_carserver.sh
./run_flightserver.sh
./run_server.sh localhost localhost localhost 1018
```

To run the RMI client locally:

```
cd Client
localhost:1018/group_18_MwServer
```
To run the whole project on cloud:
- You need to have set up public key with you SOCS account
- You need to add key for remote location to know_hosts
- You need to clone/pull the project on cloud
- You need to build the project on cloud
- You need to verify the port is available
- Remember to change the account name inside the file
```
at local
cd Middleware/
./run_servers.sh 
```


