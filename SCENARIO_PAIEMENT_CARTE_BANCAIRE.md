# 🏦 Scénario Complet : Commande avec Paiement par Carte Bancaire

## 📱 Vue d'ensemble : Du Front-End au Back-End

Ce document explique le parcours complet d'un utilisateur qui passe une commande et paie par **CARTE BANCAIRE EXTERNE** (pas avec le crédit étudiant).

---

## 🎬 Scénario Complet de A à Z

### 👤 Acteur : Marie (Étudiante à Polytech)
- **Crédit étudiant** : 10€ (insuffisant pour sa commande)
- **Carte bancaire** : Oui (Visa)
- **Objectif** : Commander un menu à 25€

---

## 📍 ÉTAPE 1 : Navigation et Sélection (Front-End)

### 1.1 Marie ouvre l'application
```
Front-End → GET /api/restaurants
Back-End → BrowseRestaurantsUseCase
Retour → Liste des restaurants
```

**Ce qui se passe :**
- L'application affiche tous les restaurants disponibles
- Marie voit : "Pizza Bella", "Burger King", "Sushi Master"

### 1.2 Marie choisit "Burger King"
```
Front-End → GET /api/restaurants/123
Back-End → GetRestaurantDetailsUseCase
Retour → Détails du restaurant + menu (liste des plats)
```

**Affichage :**
```
🍔 Burger King
─────────────────────
☰ Menu:
  - Whopper Menu      18.00€
  - Big King XXL      25.00€ ⭐
  - Nuggets x10       12.00€
  - Frites Large       4.50€
```

### 1.3 Marie sélectionne "Big King XXL" (25€)
```
Front-End → Affiche un bouton "Ajouter au panier"
Marie clique → Quantité: 1
```

---

## 🛒 ÉTAPE 2 : Ajout au Panier (Front-End + Back-End)

### 2.1 Requête d'ajout au panier
```http
POST /api/cart/add
Content-Type: application/json

{
  "userId": "marie-001",
  "dishId": "dish-big-king-xxl",
  "restaurantId": "123",
  "quantity": 1
}
```

### 2.2 Traitement Back-End
```java
AddDishToCartUseCase.execute(request)
├─ Vérifier si Marie a déjà un panier actif
│  └─ Non → Créer un nouveau panier
│  └─ Oui → Utiliser le panier existant
├─ Récupérer le plat "Big King XXL" (25€)
├─ Créer un CartItem (dish, quantity=1)
├─ Ajouter au panier
└─ Sauvegarder dans CartRepository
```

### 2.3 Réponse au Front-End
```json
{
  "success": true,
  "cartId": "cart-567",
  "totalItems": 1,
  "totalAmount": 25.00,
  "message": "Big King XXL ajouté au panier"
}
```

### 2.4 Affichage Front-End
```
🛒 Panier (1)
─────────────────────
Big King XXL      25.00€
Quantité: 1

Total: 25.00€

[Passer la commande →]
```

---

## 💳 ÉTAPE 3 : Choix du Mode de Paiement (Front-End)

### 3.1 Marie clique sur "Passer la commande"

**Front-End affiche une modal/page de paiement :**

```
┌─────────────────────────────────┐
│  💰 Choisissez votre paiement   │
├─────────────────────────────────┤
│                                 │
│  ⚪ Crédit Étudiant             │
│     Solde disponible: 10.00€    │
│     ❌ Insuffisant (25€ requis) │
│                                 │
│  🔵 Carte Bancaire              │
│     ✅ Disponible               │
│     [Sélectionné]               │
│                                 │
├─────────────────────────────────┤
│  Total à payer: 25.00€          │
│                                 │
│  [Annuler]  [Confirmer →]       │
└─────────────────────────────────┘
```

### 3.2 Vérification en temps réel (optionnelle)

**Front-End peut vérifier avant la validation :**
```http
POST /api/payment/check
Content-Type: application/json

{
  "userId": "marie-001",
  "amount": 25.00,
  "paymentMethod": "EXTERNAL_CARD"
}
```

**Réponse :**
```json
{
  "canPay": true,
  "message": "Paiement par carte bancaire disponible"
}
```

---

## ✅ ÉTAPE 4 : Validation de la Commande (Front-End → Back-End)

### 4.1 Marie clique sur "Confirmer"

**Requête HTTP envoyée :**
```http
POST /api/orders/place
Content-Type: application/json
Authorization: Bearer {token-de-marie}

{
  "userId": "marie-001",
  "restaurantId": "123",
  "paymentMethod": "EXTERNAL_CARD"
}
```

---

## 🔄 ÉTAPE 5 : Traitement Back-End (PlaceOrderUseCase)

Voici le **flux complet** dans le code :

### 5.1 Validation initiale
```java
PlaceOrderUseCase.execute(request) {
    
    // 1️⃣ Vérifier la validité de la requête
    if (!request.isValid()) {
        throw IllegalArgumentException("Invalid request");
    }
    
    // 2️⃣ Vérifier qu'il n'y a pas de commande active
    boolean hasActiveOrder = orderRepository.existsActiveOrderByUserId("marie-001");
    if (hasActiveOrder) {
        throw ValidationException("Vous avez déjà une commande en cours");
    }
    // ✅ Marie n'a pas de commande en cours
```

### 5.2 Récupération des données
```java
    // 3️⃣ Récupérer Marie
    User marie = userRepository.findById("marie-001");
    // marie.studentCredit = 10.00€
    
    // 4️⃣ Récupérer le panier de Marie
    Cart cart = cartRepository.findActiveCartByUserId("marie-001");
    // cart contient: 1x Big King XXL (25€)
    
    // 5️⃣ Vérifier que le panier n'est pas vide
    if (cart.isEmpty()) {
        throw ValidationException("Le panier est vide");
    }
    // ✅ Le panier contient 1 item
    
    // 6️⃣ Récupérer le restaurant
    Restaurant restaurant = restaurantRepository.findById("123");
    // restaurant = Burger King
```

### 5.3 Calcul du montant total
```java
    // 7️⃣ Transformer CartItems → OrderItems
    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal totalAmount = cart.calculateTotal();
    // totalAmount = 25.00€
    
    for (CartItem cartItem : cart.getItems()) {
        Dish dish = restaurant.findDishById(cartItem.getDishId());
        // dish = Big King XXL (25€)
        
        OrderItem orderItem = new OrderItem(dish, cartItem.getQuantity());
        // orderItem = Big King XXL x1
        
        orderItems.add(orderItem);
    }
```

---

## 💳 ÉTAPE 6 : Traitement du Paiement (Strategy Pattern)

### 6.1 Création du contexte de paiement
```java
    // 8️⃣ Créer le PaymentContext avec la stratégie EXTERNAL_CARD
    PaymentContext paymentContext = new PaymentContext(PaymentMethod.EXTERNAL_CARD);
    
    // 🔧 En interne, PaymentContext fait:
    // PaymentStrategyFactory.createStrategy(EXTERNAL_CARD)
    //   → return new ExternalCardStrategy();
```

**Diagramme :**
```
PaymentContext
     │
     ├─ paymentMethod = EXTERNAL_CARD
     │
     └─ strategy = ExternalCardStrategy
              │
              ├─ processPayment()
              ├─ canPay()
              └─ getStrategyName()
```

### 6.2 Vérification de la capacité de paiement
```java
    // 9️⃣ Vérifier si Marie peut payer avec sa carte
    if (!paymentContext.canUserPay(marie, 25.00€)) {
        throw InsufficientCreditException("...");
    }
    
    // 🔧 En interne, cela appelle:
    // ExternalCardStrategy.canPay(marie, 25.00€)
```

**Code de ExternalCardStrategy.canPay() :**
```java
@Override
public boolean canPay(User user, BigDecimal amount) {
    if (user == null || amount == null) {
        return false;
    }
    
    // Pour la carte bancaire externe, on suppose toujours disponible
    // (la vérification réelle se fait avec l'API bancaire lors du paiement)
    return amount.compareTo(BigDecimal.ZERO) > 0;
}
// Retour: true ✅ (25€ > 0€)
```

### 6.3 Exécution du paiement
```java
    // 🔟 Traiter le paiement via la stratégie
    PaymentResult paymentResult = paymentContext.executePayment(25.00€, marie);
    
    // 🔧 En interne, cela appelle:
    // ExternalCardStrategy.processPayment(25.00€, marie)
```

**Code de ExternalCardStrategy.processPayment() :**
```java
@Override
public PaymentResult processPayment(BigDecimal amount, User user) {
    // 1️⃣ Validation
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        return PaymentResult.failure("Montant invalide", "INVALID_AMOUNT");
    }
    
    if (user == null) {
        return PaymentResult.failure("Utilisateur invalide", "INVALID_USER");
    }
    
    // 2️⃣ Simuler l'appel à une API bancaire externe
    try {
        // Dans un vrai système, ici on appellerait :
        // - Stripe API
        // - PayPal API
        // - API bancaire
        
        // Exemple d'appel (simulé):
        // ExternalPaymentAPI.charge(user.getCreditCard(), amount);
        
        // Génération d'un ID de transaction
        String transactionId = "EXT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // transactionId = "EXT-A7F9C2E1"
        
        // 3️⃣ Simuler une réponse réussie
        return PaymentResult.success(
            transactionId,
            amount,
            String.format("Paiement de %.2f€ effectué avec succès par carte bancaire",
                amount.doubleValue())
        );
        // Message: "Paiement de 25.00€ effectué avec succès par carte bancaire"
        
    } catch (Exception e) {
        return PaymentResult.failure(
            "Erreur lors du paiement par carte: " + e.getMessage(),
            "EXTERNAL_PAYMENT_ERROR"
        );
    }
}
```

**Retour :**
```java
PaymentResult {
    success: true,
    transactionId: "EXT-A7F9C2E1",
    message: "Paiement de 25.00€ effectué avec succès par carte bancaire",
    processedAmount: 25.00,
    timestamp: 2025-10-20T14:35:22,
    errorCode: null
}
```

### 6.4 Vérification du résultat
```java
    // 1️⃣1️⃣ Vérifier le résultat du paiement
    if (!paymentResult.success()) {
        throw ValidationException("Échec du paiement: " + paymentResult.message());
    }
    // ✅ Paiement réussi !
```

---

## 📦 ÉTAPE 7 : Création et Sauvegarde de la Commande

### 7.1 Sauvegarde de l'utilisateur
```java
    // 1️⃣2️⃣ Sauvegarder Marie
    userRepository.save(marie);
    
    // Note: Pour la carte bancaire, le crédit étudiant N'EST PAS modifié
    // marie.studentCredit reste à 10.00€
```

### 7.2 Création de la commande
```java
    // 1️⃣3️⃣ Créer la commande
    Order order = new Order(
        marie,                      // Utilisateur
        restaurant,                 // Burger King
        orderItems,                 // [Big King XXL x1]
        PaymentMethod.EXTERNAL_CARD // Méthode de paiement
    );
    
    // 🔧 Le constructeur Order initialise:
    // - orderId (UUID généré)
    // - status = PENDING
    // - orderDateTime = maintenant
    // - totalAmount = 25.00€
```

### 7.3 Sauvegarde de la commande
```java
    // 1️⃣4️⃣ Sauvegarder la commande
    Order savedOrder = orderRepository.save(order);
    
    // savedOrder = {
    //   orderId: "order-789",
    //   user: marie,
    //   restaurant: Burger King,
    //   items: [Big King XXL x1],
    //   totalAmount: 25.00€,
    //   status: PENDING,
    //   paymentMethod: EXTERNAL_CARD,
    //   orderDateTime: 2025-10-20T14:35:22
    // }
```

### 7.4 Suppression du panier
```java
    // 1️⃣5️⃣ Vider le panier
    cartRepository.delete(cart);
    
    // Le panier de Marie est maintenant vide
```

---

## 📤 ÉTAPE 8 : Réponse au Front-End

### 8.1 Création de la réponse
```java
    // 1️⃣6️⃣ Retourner la réponse
    return new PlaceOrderResponse(
        savedOrder.getOrderId(),           // "order-789"
        savedOrder.getUser().getName(),    // "Marie"
        savedOrder.getRestaurant().getName(), // "Burger King"
        savedOrder.getTotalAmount(),       // 25.00€
        savedOrder.getStatus(),            // PENDING
        savedOrder.getPaymentMethod(),     // EXTERNAL_CARD
        savedOrder.getOrderDateTime()      // 2025-10-20T14:35:22
    );
}
```

### 8.2 Réponse HTTP
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "orderId": "order-789",
  "userName": "Marie",
  "restaurantName": "Burger King",
  "totalAmount": 25.00,
  "status": "PENDING",
  "paymentMethod": "EXTERNAL_CARD",
  "orderDateTime": "2025-10-20T14:35:22",
  "transactionId": "EXT-A7F9C2E1"
}
```

---

## 🎉 ÉTAPE 9 : Confirmation Front-End

### 9.1 Affichage de la confirmation
```
┌─────────────────────────────────────┐
│  ✅ Commande Confirmée !            │
├─────────────────────────────────────┤
│                                     │
│  Commande #order-789                │
│  Restaurant: Burger King            │
│                                     │
│  📦 Articles:                       │
│    • Big King XXL x1      25.00€    │
│                                     │
│  💳 Paiement:                       │
│    Carte Bancaire ✅                │
│    Transaction: EXT-A7F9C2E1        │
│                                     │
│  💰 Total: 25.00€                   │
│                                     │
│  📅 Date: 20/10/2025 14:35          │
│  🔄 Statut: En préparation          │
│                                     │
│  Temps estimé: 30-45 minutes        │
│                                     │
│  [Suivre ma commande →]             │
└─────────────────────────────────────┘
```

### 9.2 Notification (optionnelle)
```
📧 Email envoyé à marie@polytech.fr
💬 SMS envoyé : "Votre commande #order-789 est confirmée !"
🔔 Notification push sur l'app mobile
```

---

## 📊 Diagramme de Séquence Complet

```
Marie        Front-End       Back-End        PaymentContext    ExternalCardStrategy    Database
  │              │               │                  │                    │                │
  ├─ Choisir ──→│               │                  │                    │                │
  │  Big King    │               │                  │                    │                │
  │              │               │                  │                    │                │
  ├─ Ajouter ───→│─POST /cart──→│                  │                    │                │
  │  au panier   │               ├─ AddToCart ────→│                    │                │
  │              │←─ 200 OK ────│                  │                    │                │
  │              │               │                  │                    │                │
  ├─ Passer ────→│               │                  │                    │                │
  │  commande    │               │                  │                    │                │
  │              │               │                  │                    │                │
  ├─ Choisir ───→│               │                  │                    │                │
  │  CARTE       │               │                  │                    │                │
  │  BANCAIRE    │               │                  │                    │                │
  │              │               │                  │                    │                │
  ├─ Confirmer ─→│─POST /orders→│                  │                    │                │
  │              │               ├─ PlaceOrder ────→│                    │                │
  │              │               │                  │                    │                │
  │              │               │         new PaymentContext(EXTERNAL_CARD)             │
  │              │               │                  ├──────────────────→│                │
  │              │               │                  │                    │                │
  │              │               │         canUserPay(marie, 25€)       │                │
  │              │               │                  ├──────────────────→│                │
  │              │               │                  │←───── true ───────┤                │
  │              │               │                  │                    │                │
  │              │               │         executePayment(25€, marie)   │                │
  │              │               │                  ├──────────────────→│                │
  │              │               │                  │                    ├─ [Simuler] ──→│
  │              │               │                  │                    │   API         │
  │              │               │                  │                    │   bancaire    │
  │              │               │                  │                    │                │
  │              │               │                  │←─ PaymentResult ──┤                │
  │              │               │                  │   (success=true)   │                │
  │              │               │                  │                    │                │
  │              │               │←─────────────────┤                    │                │
  │              │               │                  │                    │                │
  │              │               ├────────────────────────────────── Save Order ────────→│
  │              │               ├────────────────────────────────── Delete Cart ───────→│
  │              │               │                  │                    │                │
  │              │←─ 200 OK ────│                  │                    │                │
  │              │  (order-789)  │                  │                    │                │
  │              │               │                  │                    │                │
  │←─ ✅ ───────┤               │                  │                    │                │
     Confirmé!
```

---

## 🔑 Points Clés à Retenir

### ✅ Avantages de la Carte Bancaire
1. **Pas de limite** : Contrairement au crédit étudiant (10€), pas de limite de montant
2. **Indépendant du crédit étudiant** : Le crédit de Marie reste intact (10€)
3. **Sécurisé** : Transaction via API bancaire externe

### ✅ Rôle du Strategy Pattern
- `PaymentContext` sélectionne automatiquement `ExternalCardStrategy`
- Le `PlaceOrderUseCase` ne connaît pas les détails d'implémentation
- Facile d'ajouter d'autres méthodes (Apple Pay, PayPal, etc.)

### ✅ Comparaison avec Crédit Étudiant

| Aspect | Crédit Étudiant | Carte Bancaire |
|--------|-----------------|----------------|
| **Strategy** | StudentCreditStrategy | ExternalCardStrategy |
| **Vérification** | canPay() vérifie le solde | canPay() toujours true |
| **Traitement** | Déduit du crédit interne | Appel API externe |
| **Impact sur User** | studentCredit diminue | studentCredit inchangé |
| **Transaction ID** | STU-XXXXXXXX | EXT-XXXXXXXX |

---

## 🚀 Évolution Future

### Implémentation Réelle de l'API Bancaire

```java
// Dans ExternalCardStrategy.processPayment()
try {
    // Appel réel à Stripe par exemple
    StripeClient stripe = new StripeClient(apiKey);
    
    ChargeRequest chargeRequest = ChargeRequest.builder()
        .amount(amount.multiply(new BigDecimal("100")).intValue()) // En centimes
        .currency("eur")
        .customerId(user.getStripeCustomerId())
        .description("Commande SophiaTech Eats")
        .build();
    
    ChargeResponse response = stripe.charges().create(chargeRequest);
    
    if (response.getStatus().equals("succeeded")) {
        return PaymentResult.success(
            response.getId(),
            amount,
            "Paiement réussi"
        );
    } else {
        return PaymentResult.failure(
            "Paiement refusé",
            response.getFailureCode()
        );
    }
    
} catch (StripeException e) {
    return PaymentResult.failure(
        "Erreur bancaire: " + e.getMessage(),
        "STRIPE_ERROR"
    );
}
```

---

## 📝 Résumé du Flux

```
1. Marie sélectionne un plat (25€)
2. Ajout au panier via AddDishToCartUseCase
3. Marie choisit "Carte Bancaire" comme méthode de paiement
4. Requête POST /api/orders/place
5. PlaceOrderUseCase démarre
6. Création de PaymentContext(EXTERNAL_CARD)
   → Sélection automatique de ExternalCardStrategy
7. canUserPay() vérifie la disponibilité
8. executePayment() traite le paiement
   → ExternalCardStrategy.processPayment()
   → Simulation d'appel API bancaire
   → Génération transaction ID: EXT-A7F9C2E1
9. PaymentResult.success retourné
10. Création de la commande (Order)
11. Sauvegarde en base de données
12. Suppression du panier
13. Retour PlaceOrderResponse au front-end
14. Affichage confirmation à Marie
```

**🎯 Résultat Final :**
- ✅ Commande créée (order-789)
- ✅ Paiement effectué par carte (25€)
- ✅ Crédit étudiant inchangé (10€)
- ✅ Panier vidé
- ✅ Marie est heureuse ! 😊

---

**📚 Fichiers impliqués dans ce scénario :**
- `PlaceOrderUseCase.java` - Orchestration
- `PaymentContext.java` - Contexte du pattern Strategy
- `ExternalCardStrategy.java` - Stratégie de paiement par carte
- `PaymentResult.java` - Résultat du paiement
- `Order.java` - Entité commande
- Tous les repositories (User, Cart, Order, Restaurant)

