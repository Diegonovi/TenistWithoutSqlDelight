#Proyecto de conversion de jar a partir de un archivo data a un archivo csv, xml o json
-Proyecto de dicado a la conersion de archivos a partir de otro, cada uno con un un modelo, repositorio y servicio propio, con su respectiva implementacion
-Este proyecto utiliza una base de sqlite, ya que es una libreria de base de datos facil de ejecutar que esta basado en archivos y es facil de ejecutar y usar
-La aplicacion cuenta con un modelo de tenista con sus respectivos atributos para luego sean utilizados en los repositorios, y a partir de ahi puedan ser adaptados en funcion del tupi de archivo que se nos
ha pedido
-El servicio es el encargado de hacer que los datos se introduzcan en la base de datos y se inserten, para piosteriormente ser convertidos en archivos independientes
-Tambien tiene unas consultas que nos permitiran hacer una busqueda mas detallada en la base de datos en funcion de la busqueda que queramos hacer
-Finalmente, para los test hemos implementado mockito para la comprobacipon de metodos si han sidp ejecutados un numero concreto de veces
-Se han realizado en total un numero de 43 test, los cuales se implementan de forma correcta a la aplicacion y permiten validar esta misma
