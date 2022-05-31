Sistemas Distribuídos, edição 2021/ 2022 
Licenciatura em Engenharia Informática, 
Faculdade de Ciência e Tecnologias da Universidade de Coimbra

ucDrive, desenvolvido por:
 - João Filipe Guiomar Artur, 2019217853
 - Rui Eduardo Carvalho Marques, 2019216539

=============================================================================

Para testar as aplicações desenvolvidas, efetuar os seguintes passos.

1) 	Registar uma base de dados Postgres chamada "scoreDEI"

2) 	Para executar a aplicação web:

	2.1) Inicializar a aplicação e abrir um separador num browser com o endereço URL:

		http://localhost:8080/

	2.2) Para popular a base de dados, introduzir os seguintes URLs:

		http://localhost:8080/createData
		http://localhost:8080/rest/getTeams?league=England&season=2021
		http://localhost:8080/rest/getPlayers?league=England&season=2021

	2.3) Para efetuar autenticação:

		Utilizador administrador:
			Email: admin@mail.com
			Password: 123

		Utilizador administrador:
			Email: aaaa@mail.com
			Password: 1234

	2.4) Testar a aplicação.