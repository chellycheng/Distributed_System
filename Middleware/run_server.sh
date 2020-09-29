#Usage: ./run_server.sh [<rmi_name>]

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ CarServer.CarResourceManagerImp $1

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ FlightServer.FlightResourceManagerImp $1

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ RoomServer.RoomResourceManagerImp $1

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ CustomerServer.CustomerResourceManagerImp $1

./run_rmi.sh > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ MwServer.MwImp $1 $2 $3 $4