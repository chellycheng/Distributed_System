#Usage: ./run_server.sh [<rmi_name>]






#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ CustomerServer.CustomerResourceManagerImp $1

./run_rmi.sh > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ MwServer.MwImp $1 $2 $3 $4

#./run_rmi.sh > /dev/null 2>&1
#java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:$(pwd)/ MwServer.MwImp localhost:1018 localhost:1018 localhost:1018 1018