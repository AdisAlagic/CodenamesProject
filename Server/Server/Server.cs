using System.Net;
using System.Net.Sockets;
using System.Text;

namespace Server;

class Server
{
    private readonly TcpListener _listener;
    private TcpClient _client;
    private int i = 0;

    private Server(int port)
    {
        _listener = new TcpListener(IPAddress.Any, port);
        _listener.Start();
        Console.WriteLine("Сервер запущен. Ожидание подключений... ");
        
        while (true)
        {
           _client = _listener.AcceptTcpClient();
           Task.Run(async ()=>await ProcessClientAsync(_client));
        }
    }
    ~Server()
    {
        if (_listener != null)
        {
            _listener.Stop();
        }
    }
    static void Main(string[] args)
    {
        new Server(10000);
    }

    async Task ProcessClientAsync(TcpClient tcpClient)
    {
        Console.WriteLine("Подключение с клиентом #" + i);
        var stream = tcpClient.GetStream();
        string response = "";
        int bytesRead = -1;
        i++;
        byte[] receiveBuffer = new byte[1024];
        while (bytesRead != 0)
        {
            do
            {
                bytesRead = stream.Read(receiveBuffer);
                response = Encoding.UTF8.GetString(receiveBuffer.AsSpan(0, bytesRead));
            } 
            while (stream.DataAvailable);
            
            Console.WriteLine($"Клиент {tcpClient.Client.RemoteEndPoint} - {response}");
            
           if(response != "")
                await stream.WriteAsync(Encoding.UTF8.GetBytes(i.ToString()));
            response = "";
        }
    }
}