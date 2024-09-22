import java.util.*;
import java.net.*;
import java.io.*;

public class SincronizadorBerkeley {

    // Clase AlgoritmoDeBerkeley
    public static class AlgoritmoDeBerkeley {

        public static void SincronizarMaquinas(RegistroMaquina[] pListaDeMaquinas) throws Exception {
            int tiempoPromedio = 0;
            int indiceActual = 0;

            // Obtener el tiempo de cada máquina y calcular el promedio
            while (indiceActual < pListaDeMaquinas.length) {
                RegistroMaquina maquinaActual = pListaDeMaquinas[indiceActual];
                int tiempoActual = maquinaActual.ObtenerTiempoActual();
                tiempoPromedio += tiempoActual;
                System.out.println("Tiempo actual de la máquina " + (indiceActual + 1) + ": " + tiempoActual + " segundos.");
                indiceActual++;
            }

            // Calcular el tiempo promedio
            tiempoPromedio = Math.round(tiempoPromedio / pListaDeMaquinas.length);
            System.out.println("Tiempo promedio calculado: " + tiempoPromedio + " segundos.");

            // Actualizar el tiempo de todas las máquinas
            indiceActual = 0;
            while (indiceActual < pListaDeMaquinas.length) {
                RegistroMaquina maquinaActual = pListaDeMaquinas[indiceActual];
                maquinaActual.ActualizarTiempo(tiempoPromedio);
                indiceActual++;
            }
        }
    }

    // Clase RegistroMaquina para manejar cada máquina
    public static class RegistroMaquina {

        public InetAddress iDireccion;
        public int iPuerto;
        public DatagramSocket iSocket;

        public RegistroMaquina(InetAddress pDireccion, int pPuerto) throws Exception {
            iDireccion = pDireccion;
            iPuerto = pPuerto;
            iSocket = new DatagramSocket();
        }

        public int ObtenerTiempoActual() throws Exception {
            byte[] buffer = new byte[1024];
            String mensajeRecibido;
            String mensajeAEnviar = "REPORTAR";
            DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
            DatagramPacket paqueteAEnviar = new DatagramPacket(mensajeAEnviar.getBytes(), mensajeAEnviar.length(), iDireccion, iPuerto);

            iSocket.send(paqueteAEnviar);
            iSocket.receive(paqueteRecibido);

            mensajeRecibido = new String(paqueteRecibido.getData()).trim();
            return Integer.parseInt(mensajeRecibido);
        }

        public void ActualizarTiempo(int pNuevoTiempo) throws Exception {
            String mensajeAEnviar = Integer.toString(pNuevoTiempo);
            DatagramPacket paqueteAEnviar = new DatagramPacket(mensajeAEnviar.getBytes(), mensajeAEnviar.length(), iDireccion, iPuerto);
            iSocket.send(paqueteAEnviar);
        }
    }

    // Método principal del sincronizador
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        ArrayList<RegistroMaquina> listaDeMaquinas = new ArrayList<RegistroMaquina>();
        RegistroMaquina[] arrayDeMaquinas;
        String opcion = "";
        InetAddress direccionMaquina;
        int puertoMaquina;

        while (!opcion.equals("0")) {
            System.out.println("1 - Agregar máquina");
            System.out.println("2 - Sincronizar con algoritmo de Berkeley");
            System.out.println("0 - Salir");
            System.out.print("Ingrese opción: ");
            opcion = scanner.nextLine();

            switch (opcion) {

                case "1":
                    // Agregar nueva máquina
                    System.out.print("Dirección de la máquina: ");
                    direccionMaquina = InetAddress.getByName(scanner.nextLine());
                    System.out.print("Puerto de la máquina: ");
                    puertoMaquina = Integer.parseInt(scanner.nextLine());

                    listaDeMaquinas.add(new RegistroMaquina(direccionMaquina, puertoMaquina));
                    System.out.println("La máquina ha sido agregada exitosamente.");
                    break;

                case "2":
                    // Sincronizar máquinas usando Berkeley
                    arrayDeMaquinas = new RegistroMaquina[listaDeMaquinas.size()];
                    listaDeMaquinas.toArray(arrayDeMaquinas);
                    AlgoritmoDeBerkeley.SincronizarMaquinas(arrayDeMaquinas);
                    System.out.println("Las máquinas han sido sincronizadas exitosamente.");
                    break;

                case "0":
                    System.out.println("Hasta luego.");
                    break;

                default:
                    System.out.println("La opción ingresada no existe. Intente nuevamente.");
                    break;
            }
        }
    }
}
