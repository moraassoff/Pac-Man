#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <arpa/inet.h>

#define PORT 12345
#define MAX_CLIENTES 100

int jugadores_conectados = 0;
pthread_mutex_t lock;

void mostrar_conteo() {
    printf("Clientes conectados:\n");
    printf("Jugadores: %d\n\n", jugadores_conectados);
}

void* manejar_cliente(void* socket_desc) {
    int sock = *(int*)socket_desc;
    free(socket_desc);

    char buffer[1024];
    memset(buffer, 0, sizeof(buffer));

    int leido = recv(sock, buffer, sizeof(buffer) - 1, 0);
    if (leido <= 0) {
        close(sock);
        pthread_exit(NULL);
    }

    buffer[leido] = '\0';

    // Verificarr tipo de cliente
    if (strncmp(buffer, "tipo:jugador", 12) == 0) {
        pthread_mutex_lock(&lock);
        jugadores_conectados++;
        mostrar_conteo();
        pthread_mutex_unlock(&lock);
    }

    while (recv(sock, buffer, sizeof(buffer), 0) > 0) {}

    pthread_mutex_lock(&lock);
    jugadores_conectados--;
    mostrar_conteo();
    pthread_mutex_unlock(&lock);

    close(sock);
    pthread_exit(NULL);
}

int main() {
    int servidor_fd, cliente_fd, *nuevo_sock;
    struct sockaddr_in servidor, cliente;
    socklen_t cliente_len = sizeof(cliente);

    // Crear socket
    servidor_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (servidor_fd == -1) {
        perror("No se pudo crear el socket");
        exit(1);
    }

    servidor.sin_family = AF_INET;
    servidor.sin_addr.s_addr = INADDR_ANY;
    servidor.sin_port = htons(PORT);

    // Enlazar socket
    if (bind(servidor_fd, (struct sockaddr*)&servidor, sizeof(servidor)) < 0) {
        perror("Error al enlazar");
        exit(1);
    }

    listen(servidor_fd, MAX_CLIENTES);
    printf("Servidor escuchando en el puerto %d...\n", PORT);

    pthread_mutex_init(&lock, NULL);

    while ((cliente_fd = accept(servidor_fd, (struct sockaddr*)&cliente, &cliente_len))) {
        pthread_t hilo_cliente;
        nuevo_sock = malloc(sizeof(int));
        *nuevo_sock = cliente_fd;
        if (pthread_create(&hilo_cliente, NULL, manejar_cliente, (void*)nuevo_sock) < 0) {
            perror("No se pudo crear el hilo");
            return 1;
        }

        // Separar hilo
        pthread_detach(hilo_cliente);
    }

    close(servidor_fd);
    pthread_mutex_destroy(&lock);
    return 0;
}