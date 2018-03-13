from socket import *
import struct
import sys, struct, time, os
from curses import ascii
from pymavlink import mavutil, mavwp

def wait_heartbeat(m):
    '''wait for a heartbeat so we know the target system IDs'''
    print("Waiting for APM heartbeat")
    msg = m.recv_match(type='HEARTBEAT', blocking=True)
    print("Heartbeat from APM (system %u component %u)" % (m.target_system, m.target_system))

error = "none"
handshake = "handshake"
name = "d-001"
serialnumber = "00001"
Attachment = "A-NDVI"
data = ""
status = "post-flight"
altitude = ["","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",]
latitude = ["","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",]
longitude = ["","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",]
height = 10
imgcount = 1


print "          ,~I777777777777?=:            "
print "       :7777777?~:,,:~+I777777+         "
print "    ~7777                   7777?,      "
print "   ?777?                      ~7777:    "
print " ~777=                          I777,   "
print "                                  777=  "
print "                                   ?77= "
print "                                    777,"
print "     Core Solucoes Aereas - 2015    777?"
print "      Oracle Mapping Server V1.0    7777"
print "        By Lucas Coraca Silva       777I"
print "                                    777="
print ",:+                                ~77I "
print "7777                              :77I, "
print ":777~                            I77I   "
print "  ?777+                        ,7777    "
print "    7777~,                  ,+777I~     "
print "      I77777              777777        "
print "        ~?777777777777777777+:          "
print "             :=+I7777I?+,               "
print ""

#Conectando com o drone e baixando localizacao
master = mavutil.mavlink_connection("/dev/ttyACM1", 115200, 255)
m = master.wait_heartbeat()
print "waiting for gps fix"
#master.wait_gps_fix()
print "got fix"
m = master.wait_heartbeat()
dpos = master.location()
print("Drone location: lat: " + str(dpos.lat) + " lng: " + str(dpos.lng))
print ""

#Inciando Socket

print ""
mysock = socket(AF_INET, SOCK_STREAM)
mysock.bind(("169.254.185.103",1111))
mysock.listen(10)
mysock.setblocking(5)

#programa principal
while True:#{
    clientsock, addr = mysock.accept()
    data = clientsock.recv(1024)
    #handshake com android
    if(data=="handshake"):#{
	print "Received handshake"
        clientsock.sendall(handshake + "\n")
        clientsock.sendall(name + "\n")
        clientsock.sendall(serialnumber + "\n")
        clientsock.sendall(Attachment + "\n")
        clientsock.sendall(status + "\n")
        data = ""#}
    else:#{
	#baixando coordenadas e altitudes e enviando para o drone
        if(data=="wsend"):#{
	    print "Received wsend"
            print ""
            numberofwayp = clientsock.recv(1024)
            size = int(numberofwayp)
	    #loop de download do celular
            for x in xrange(0, size):#{
                latitude[x] = clientsock.recv(1024)
                longitude[x] = clientsock.recv(1024)
                altitude[x] = clientsock.recv(1024)
                print("Received lat-" + str(x) + " " + latitude[x])
                print("Received lng-" + str(x) + " " + longitude[x])
                print("Received alt-" + str(x) + " " + altitude[x])
		print(" ")#}
            master.wait_heartbeat(blocking=True)
            wp = mavwp.MAVWPLoader()
            seq = 2
            frame = mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT
            radius = 10
            wp.add(mavutil.mavlink.MAVLink_mission_item_message(master.target_system,master.target_component,1,frame,mavutil.mavlink.MAV_CMD_NAV_WAYPOINT,0, 0, 0, radius, 0, 0,dpos.lat,dpos.lng,float(altitude[0]) + height))
	    #loop de criacao objeto waypoint
            for i in xrange(0, size):#{
                if(i==size-1):#{
			wp.add(mavutil.mavlink.MAVLink_mission_item_message(master.target_system,master.target_component,seq,frame,mavutil.mavlink.MAV_CMD_NAV_RETURN_TO_LAUNCH,0, 0, 0, radius, 0, 0,float(latitude[i]),float(longitude[i]),float(altitude[i]) + height))#}
		else:#{
                	wp.add(mavutil.mavlink.MAVLink_mission_item_message(master.target_system,master.target_component,seq,frame,mavutil.mavlink.MAV_CMD_NAV_WAYPOINT,0, 0, 0, radius, 0, 0,float(latitude[i]),float(longitude[i]),float(altitude[i]) + height))
                	seq += 1#}#}
            master.waypoint_clear_all_send()
            master.waypoint_count_send(wp.count())
	    data = ""
	    #loop de envio de waypoint para o drone
            for i in range(wp.count()):#{
            	msg = master.recv_match(type=['MISSION_REQUEST'],blocking=True)
            	master.mav.send(wp.wp(msg.seq))
            	print 'Sending waypoint {0}'.format(msg.seq)#}#}#}
	if(data=="takeoff"):#{
		print "Received Take-Off"
		m = master.wait_heartbeat()
		while m.base_mode < 200:#{
			m = master.wait_heartbeat()
		#}
		armcheck = True
		while armcheck:#{
			m = master.wait_heartbeat()
			if m.base_mode < 200:#{
				armcheck = False
			#}
			currway = master.waypoint_current()
			os.system("fswebcam -r 640x480 --no-banner /home/pi/orcaulo/Scan/" + str(imgcount) + ".jpg")
			print(currway)
			imgcount= imgcount +1 #}
		imgcount=0
		data = ""
	    #}
    #}
#}
