#!/bin/bash 

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab2--8, lab2-10, etc...)
#MACHINES=(teach-vw1.cs.mc teach-vw2 teach-vw3 teach-vw4)
MACHINES=(lab2-20.cs.mcgill.ca lab2-21.cs.mcgill.ca lab2-22.cs.mcgill.ca lab2-23.cs.mcgill.ca lab2-25.cs.mcgill.ca)
tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 1 \; \
	send-keys "ssh -t hcheng35@${MACHINES[0]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to  CarServer'; ./run_carserver.sh 1048\"" C-m \; \
	select-pane -t 2 \; \
	send-keys "ssh -t hcheng35@${MACHINES[1]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to FlightServer'; ./run_flightserver.sh 1048\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t hcheng35@${MACHINES[2]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to RoomServer'; ./run_roomserver.sh 1048\"" C-m \; \
	select-pane -t 4 \; \
	send-keys "ssh -t hcheng35@${MACHINES[3]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Middleware > /dev/null; echo -n 'Connected to MiddlewareServer'; sleep .5s; ./run_server.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]} 1048\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "ssh -t hcheng35@${MACHINES[4]} \"cd /home/2018/hcheng35/COMP512/Distributed_System/Client > /dev/null; echo -n 'Connected to '; Client; sleep .5s; ./run_client.sh ${MACHINES[3]} MwServer 1048\"" C-m \;
