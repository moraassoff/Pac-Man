// servidor.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#include <windows.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 12345
#define MAX_CLIENTES 100

int jugadores_conectados = 0;
CRITICAL_SECTION lock;

DWORD WINAPI manejar_cliente(LPVOID cliente_socket_ptr);

void mostrar_conteo() {
    printf("Clientes conectados:\n");
    printf("Jugadores: %d\n\n", jugadores_conectados);
}

int main() {
    WSADATA wsa;
    SOCKET servidor, cliente;
    struct sockaddr_in servidor_addr, cliente_addr;
    int cliente_len = sizeof(cliente_addr);
    char buffer[1024];

    WSAStartup(MAKEWORD(2, 2), &wsa);

    servidor = socket(AF_INET, SOCK_STREAM, 0);
    servidor_addr.sin_family = AF_INET;
    servidor_addr.sin_addr.s_addr = INADDR_ANY;
    servidor_addr.sin_port = htons(12345);

    bind(servidor, (struct sockaddr *)&servidor_addr, sizeof(servidor_addr));
    listen(servidor, 1);

    printf("Servidor esperando conexiones en el puerto 12345...\n");

    cliente = accept(servidor, (struct sockaddr *)&cliente_addr, &cliente_len);
    printf("Cliente conectado desde: %s\n", inet_ntoa(cliente_addr.sin_addr));

    // leer nombre del cliente
    int recibido = recv(cliente, buffer, sizeof(buffer) - 1, 0);
    if (recibido > 0) {
        buffer[recibido] = '\0';
        printf("Nombre del cliente: %s\n", buffer);
    }

    closesocket(cliente);
    closesocket(servidor);
    WSACleanup();
    return 0;
}

DWORD WINAPI manejar_cliente(LPVOID cliente_socket_ptr) {
    SOCKET cliente_socket = *(SOCKET*)cliente_socket_ptr;
    free(cliente_socket_ptr);

    char buffer[1024];
    memset(buffer, 0, sizeof(buffer));

    int leido = recv(cliente_socket, buffer, sizeof(buffer) - 1, 0);
    if (leido <= 0) {
        closesocket(cliente_socket);
        return 0;
    }

    buffer[leido] = '\0';

    if (strncmp(buffer, "tipo:jugador", 12) == 0) {
        EnterCriticalSection(&lock);
        jugadores_conectados++;
        mostrar_conteo();
        LeaveCriticalSection(&lock);
    }

    // mantienee cliente hasta desconectarse
    while (recv(cliente_socket, buffer, sizeof(buffer), 0) > 0) {}

    EnterCriticalSection(&lock);
    jugadores_conectados--;
    mostrar_conteo();
    LeaveCriticalSection(&lock);

    closesocket(cliente_socket);
    return 0;
}
