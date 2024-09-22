import java.net.*;
import java.util.Scanner;

public class MaquinaBerkeley {
    private int segundosActuales;
    private DatagramSocket socket;

    public MaquinaBerkeley(InetAddress direccion, int puerto, int segundosIniciales) throws Exception {
        socket = new DatagramSocket(puerto, direccion);
        segundosActuales = segundosIniciales;
    }

    public void notificarTiempoActual(InetAddress direccion, int puerto) throws Exception {
        String datosAEnviar = Integer.toString(segundosActuales);
        DatagramPacket paqueteAEnviar = new DatagramPacket(datosAEnviar.getBytes(), datosAEnviar.length(), direccion, puerto);
        socket.send(paqueteAEnviar);
    }

    public int avanzarTiempo() throws Exception {
        int MAXIMOS_MILISEGUNDOS = 1500;
        byte[] buffer = new byte[1024];
        DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
        String mensajeRecibido;

        int milisegundosActuales = (int) Math.round(MAXIMOS_MILISEGUNDOS * Math.random());

        try {
            socket.setSoTimeout(milisegundosActuales);
            socket.receive(paqueteRecibido);
            mensajeRecibido = new String(paqueteRecibido.getData()).trim();
            
            if (mensajeRecibido.equals("REPORTAR")) {
                notificarTiempoActual(paqueteRecibido.getAddress(), paqueteRecibido.getPort());
            } else {
                segundosActuales = Integer.parseInt(mensajeRecibido);
            }
        } catch (SocketTimeoutException e) {
            socket.setSoTimeout(0);
            segundosActuales++;
        }
        return segundosActuales;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("La máquina funciona en la dirección: ");
        InetAddress direccionMaquina = InetAddress.getByName(scanner.nextLine());
        System.out.print("En el puerto: ");
        int puertoMaquina = Integer.parseInt(scanner.nextLine());

        MaquinaBerkeley maquina = new MaquinaBerkeley(direccionMaquina, puertoMaquina, 0);
        System.out.println("La máquina está funcionando en el puerto " + puertoMaquina + " de la dirección " + direccionMaquina);

        while (true) {
            int tiempo = maquina.avanzarTiempo();
            System.out.println("Tiempo actual de la máquina: " + tiempo + " segundos");
        }
    }
}
