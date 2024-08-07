<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.azienda.shop.model.Product, com.azienda.shop.model.User" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grazie per il tuo acquisto!</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/confermaAcquistoStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
</head>
<body>
<div class="container">
    <h1>Grazie per il tuo acquisto!</h1>

    <!-- Informazioni per la spedizione -->
    <h5><i class="fas fa-box"></i> Informazioni per la tua spedizione</h5>
    <div class="user-info">
        <p><strong>Nome citofono:</strong> <%= ((User) request.getAttribute("user")).getUsername() %></p>
        <p><strong>Email:</strong> <%= ((User) request.getAttribute("user")).getEmail() %></p>
        <p><strong>Indirizzo:</strong> <%= ((User) request.getAttribute("user")).getAddress() %></p>
    </div>

    <!-- Prodotto acquistato -->
    <div class="products">
        <h2>Prodotto comprato:</h2>
        <ul>
            <li><%= ((Product) request.getAttribute("product")).getName() %></li>
        </ul>
    </div>

    <!-- Informazioni aggiuntive sulla spedizione -->
    <p class="soon"><i class="fas fa-truck"></i> Arriverà presto</p>
    <!-- Bottone di ritorno alla home -->
    <div class="back-home">
        <form action="${pageContext.request.contextPath}/index" method="POST">
            <button type="submit" class="btn btn-outline-dark">Torna alla Home</button>
        </form>
    </div>



</div>
</body>
</html>
