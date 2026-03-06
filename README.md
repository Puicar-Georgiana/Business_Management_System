Order Management System
Acest proiect este o aplicație Java structurată pe straturi, concepută pentru gestionarea eficientă a clienților, produselor și comenzilor. Arhitectura separă clar logica de business de interacțiunea cu baza de date, asigurând modularitate și ușurință în mentenanță.

Structura Aplicației
Sistemul este organizat în patru pachete principale:

BusinessLogic: Acesta reprezintă stratul de decizie al aplicației. Aici sunt implementate regulile de business și coordonarea proceselor. De exemplu, în clasa OrderBLL, procesul de creare a unei comenzi nu doar salvează datele, ci verifică automat disponibilitatea stocului, actualizează stocul produsului și generează factura aferentă.

Data_Access: Responsabil de comunicarea directă cu baza de date MySQL. Utilizează un model generic, AbstractDAO, care folosește Java Reflection pentru a automatiza operațiunile de bază (CRUD) pentru orice entitate din sistem, reducând astfel duplicarea codului.

Model: Definește structura entităților utilizate în sistem (Bill, Client, Order, Product). Este pachetul care găzduiește obiectele de date (inclusiv records pentru o gestionare imutabilă a facturilor).

Connection: Gestionează ciclul de viață al conexiunilor la baza de date prin clasa ConnectionFactory, oferind metode sigure pentru deschiderea și închiderea resurselor SQL.

Logica de Business
Pachetul BusinessLogic acționează ca un intermediar între utilizator și baza de date. Fiecare clasă BLL (BillBLL, ClientBLL, OrderBLL, ProductBLL) oferă metode specifice pentru a manipula entitățile corespunzătoare.

Punctul central al aplicației este OrderBLL, care asigură integritatea datelor prin validări stricte. În momentul în care o comandă este plasată, clasa se asigură că:

Produsul există și stocul este suficient.

Comanda este salvată în baza de date.

Stocul produsului este decrementat corespunzător.

O factură este creată automat pentru evidența tranzacției.

Configurare și Cerințe
Pentru a rula acest proiect, asigurați-vă că aveți instalat un mediu de dezvoltare Java (versiunea 17 sau mai nouă) și o instanță de MySQL activă.

Baza de date trebuie să se numească order_management. Datele de autentificare (utilizator și parolă) pot fi actualizate în clasa ConnectionFactory pentru a se potrivi cu configurarea locală a serverului dumneavoastră de date. De asemenea, este necesară prezența driverului JDBC MySQL în bibliotecile proiectului.
