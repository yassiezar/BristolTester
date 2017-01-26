import net, json, system, times

const port: int = 6666

proc getFileName(): string = 
  
  var fileName = getDateStr()

  fileName = fileName & "_" & getClockStr()

  return fileName & ".csv"

proc main() = 

  var 
    socket: Socket = newSocket()
    clientSocket: Socket = newSocket()

    readLineSuccess: int = 0
    clientAddr: string = ""
    readString: string = ""
    part: string = " "

    file = open(getFileName(), fmWrite)

  socket.bindAddr(Port(port))
  socket.listen()

  while true:
    socket.acceptAddr(clientSocket, clientAddr)
    echo "Connected from ", clientAddr

    readString = ""
    part = "0"

    while part != "":
      readLineSuccess = recv(clientSocket, part, size=1024)
      readString = readString & part

    file.write(readString)
    echo "Wrote ", readString

  file.close()

main()
