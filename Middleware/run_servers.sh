#!/bin/bash 

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab2--8, lab2-10, etc...)
#MACHINES=(teach-vw1.cs.mc teach-vw2 teach-vw3 teach-vw4)
MACHINES=(hcheng35@lab2-20.cs.mcgill.ca hcheng35@lab2-21.cs.mcgill.ca hcheng35@lab2-22.cs.mcgill.ca hcheng35@lab2-23.cs.mcgill.ca)
tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 1 \; \
	send-keys "sshpass -p $1 ssh -t ${MACHINES[0]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to '; CarServer; ./run_carserver.sh 1048\"" C-m \; \
	select-pane -t 2 \; \
	send-keys "sshpass -p $1 ssh -t ${MACHINES[1]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to '; FlightServer; ./run_flightserver.sh 1048\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "sshpass -p $1 ssh -t ${MACHINES[2]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to '; RoomServer; ./run_roomserver.sh 1048\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "sshpass -p $1 ssh -t ${MACHINES[3]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to '; MiddlewareServer; sleep .5s; ./run_server.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]} 1048\"" C-m \;
