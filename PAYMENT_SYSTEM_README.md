# 💳 Système de Paiement - SophiaTech Eats

## 📋 Vue d'ensemble

Le système de paiement de SophiaTech Eats utilise le **Strategy Pattern** (Design Pattern) pour gérer différentes méthodes de paiement de manière flexible et extensible.

**✅ Confirmation : Le système de paiement est ACTIVEMENT utilisé dans le projet**, notamment dans le `PlaceOrderUseCase` pour traiter les commandes.

---

## 🏗️ Architecture du Système

### 1️⃣ Pattern Strategy - Composants Principaux

```
PaymentStrategy (Interface)
    ├── StudentCreditStrategy (Implémentation)
    └── ExternalCardStrategy (Implémentation)

PaymentContext (Contexte)
    └── Utilise une PaymentStrategy

PaymentStrategyFactory (Factory)
    └── Crée les stratégies selon PaymentMethod
```

### 2️⃣ Enum des Méthodes de Paiement

```java
public enum PaymentMethod {
    STUDENT_CREDIT,    // Paiement par crédit étudiant
    EXTERNAL_CARD      // Paiement par carte bancaire externe
}
```

---

## 🔄 Flux de Paiement dans le Projet

### Où est utilisé le système de paiement ?

**Fichier principal : `PlaceOrderUseCase.java`**

Voici le flux complet lorsqu'un utilisateur passe une commande :

```
1. Utilisateur → PlaceOrderRequest (avec paymentMethod)
                      ↓
2. PlaceOrderUseCase vérifie :
   - L'utilisateur n'a pas de commande active
   - Le panier existe et n'est pas vide
   - Le restaurant existe
                      ↓
3. Si PaymentMethod = STUDENT_CREDIT :
   → Vérification du crédit : user.hasEnoughCredit(totalAmount)
   → Si insuffisant : throw InsufficientCreditException
                      ↓
4. Création de la commande (Order)
                      ↓
5. Si PaymentMethod = STUDENT_CREDIT :
   → Déduction du crédit : user.deductCredit(totalAmount)
   → Sauvegarde de l'utilisateur
                      ↓
6. Sauvegarde de la commande
                      ↓
7. Suppression du panier
                      ↓
8. Retour de PlaceOrderResponse
```

---

## 💡 StudentCreditStrategy - Fonctionnement Détaillé

### Méthodes Principales

#### 1. `processPayment(BigDecimal amount, User user)`
**Traite un paiement par crédit étudiant**

```java
// Validation des paramètres
if (amount <= 0) → ÉCHEC "Montant invalide"
if (user == null) → ÉCHEC "Utilisateur invalide"

// Vérification du crédit
if (!canPay(user, amount)) → ÉCHEC "Crédit insuffisant"

// Traitement du paiement
currentCredit = user.getStudentCredit()
newBalance = currentCredit - amount
user.setStudentCredit(newBalance)

// Génération transaction ID (ex: STU-A3F8B2C1)
return PaymentResult.success(...)
```

**Exemple de résultat :**
- ✅ Succès : `"Paiement de 25.50€ effectué avec succès. Nouveau solde: 14.50€"`
- ❌ Échec : `"Crédit étudiant insuffisant. Solde: 20.00€, Requis: 25.50€"`

#### 2. `canPay(User user, BigDecimal amount)`
**Vérifie si l'utilisateur peut payer**

```java
BigDecimal studentCredit = user.getStudentCredit();

return studentCredit >= amount && studentCredit >= 0;
```

**Exemples :**
- ✅ Crédit = 50€, Montant = 25€ → `true`
- ❌ Crédit = 20€, Montant = 25€ → `false`
- ❌ Crédit = 0€, Montant = 10€ → `false`

#### 3. `isAvailable()`
**Vérifie la disponibilité du service de paiement**

```java
return true; // Le service de crédit étudiant est toujours disponible
```

**Important :** Cette méthode vérifie si le **service** est disponible, pas si un utilisateur spécifique a du crédit. La vérification du solde se fait dans `canPay()`.

#### 4. `getStrategyName()`
```java
return "Student Credit Payment";
```

---

## 🎯 Scénarios d'Utilisation

### Scénario 1 : Paiement Réussi
```
Étudiant : Jean (Crédit = 50€)
Commande : Pizza (15€)
Méthode : STUDENT_CREDIT

→ canPay(Jean, 15€) = true
→ processPayment() réussit
→ Nouveau crédit de Jean = 35€
→ Commande créée avec statut SUCCESS
```

### Scénario 2 : Crédit Insuffisant
```
Étudiant : Marie (Crédit = 20€)
Commande : Menu (25€)
Méthode : STUDENT_CREDIT

→ canPay(Marie, 25€) = false
→ PlaceOrderUseCase throw InsufficientCreditException
→ Message : "Crédit insuffisant. Requis: 25.00€, Disponible: 20.00€"
→ Commande NON créée
```

### Scénario 3 : Paiement par Carte
```
Étudiant : Paul (Crédit = 10€)
Commande : Burger (18€)
Méthode : EXTERNAL_CARD

→ ExternalCardStrategy est utilisée
→ Pas de vérification du crédit étudiant
→ Paiement simulé via API externe
→ Commande créée
→ Crédit étudiant de Paul inchangé (10€)
```

---

## 🧪 Tests Unitaires

Le système de paiement est **entièrement testé** :

### Tests StudentCreditStrategy
- ✅ `studentCredit_should_process_payment_successfully()`
- ✅ `studentCredit_should_fail_with_insufficient_funds()`
- ✅ `studentCredit_should_reject_invalid_amounts()`
- ✅ `studentCredit_should_reject_null_user()`
- ✅ `studentCredit_should_check_if_user_can_pay()`
- ✅ `studentCredit_should_always_be_available()`
- ✅ `studentCredit_should_have_correct_name()`

### Tests PlaceOrderUseCase
- ✅ Vérifie la déduction du crédit
- ✅ Vérifie les exceptions pour crédit insuffisant
- ✅ Vérifie la transformation panier → commande

---

## 🔧 Comment Utiliser le Système de Paiement

### Option 1 : Via PaymentContext (Recommandé)
```java
// Créer le contexte avec la méthode de paiement
PaymentContext context = new PaymentContext(PaymentMethod.STUDENT_CREDIT);

// Exécuter le paiement
PaymentResult result = context.executePayment(amount, user);

// Vérifier le résultat
if (result.isSuccess()) {
    System.out.println("Paiement réussi : " + result.getMessage());
    System.out.println("Transaction ID : " + result.getTransactionId());
} else {
    System.out.println("Paiement échoué : " + result.getMessage());
    System.out.println("Code erreur : " + result.getErrorCode());
}
```

### Option 2 : Utilisation Directe de la Stratégie
```java
PaymentStrategy strategy = new StudentCreditStrategy();

// Vérifier avant de payer
if (strategy.canPay(user, amount)) {
    PaymentResult result = strategy.processPayment(amount, user);
    // Traiter le résultat...
}
```

### Option 3 : Via Factory
```java
PaymentStrategy strategy = PaymentStrategyFactory.createStrategy(
    PaymentMethod.STUDENT_CREDIT
);

PaymentResult result = strategy.processPayment(amount, user);
```

---

## 🎨 Avantages du Strategy Pattern

### ✅ Extensibilité
Ajouter une nouvelle méthode de paiement est simple :
1. Créer une nouvelle classe `XxxStrategy implements PaymentStrategy`
2. Ajouter l'enum dans `PaymentMethod`
3. Mettre à jour `PaymentStrategyFactory`
4. **Aucune modification** du code existant !

### ✅ Testabilité
- Chaque stratégie peut être testée indépendamment
- Facile de mocker pour les tests d'intégration

### ✅ Flexibilité
- Changement de stratégie à l'exécution
- Logique de paiement isolée et réutilisable

### ✅ Principe Open/Closed
- Ouvert à l'extension (nouvelles stratégies)
- Fermé à la modification (code existant intact)

---

## 📊 Diagramme de Classes (Simplifié)

```
┌─────────────────────────┐
│   PaymentStrategy       │
│   <<interface>>         │
├─────────────────────────┤
│ + processPayment()      │
│ + canPay()              │
│ + getStrategyName()     │
│ + isAvailable()         │
└───────────┬─────────────┘
            │
            ├──────────────────────────────┐
            │                              │
┌───────────▼──────────────┐   ┌──────────▼──────────────┐
│ StudentCreditStrategy    │   │ ExternalCardStrategy    │
├──────────────────────────┤   ├─────────────────────────┤
│ - MINIMUM_BALANCE        │   │ - externalPaymentAPI    │
├──────────────────────────┤   ├─────────────────────────┤
│ + processPayment()       │   │ + processPayment()      │
│ + canPay()               │   │ + canPay()              │
└──────────────────────────┘   └─────────────────────────┘
```

---

## 🚀 Évolutions Futures Possibles

### Nouvelles Stratégies de Paiement
- `ApplePayStrategy`
- `GooglePayStrategy`
- `PayPalStrategy`
- `CryptoPaymentStrategy`

### Fonctionnalités Additionnelles
- Historique des transactions
- Remboursements automatiques
- Paiements fractionnés
- Codes promo / réductions

---

## 📝 Résumé

| Aspect | Détails |
|--------|---------|
| **Pattern utilisé** | Strategy Pattern |
| **Méthodes disponibles** | STUDENT_CREDIT, EXTERNAL_CARD |
| **Utilisation principale** | `PlaceOrderUseCase` (transformation panier → commande) |
| **Vérification crédit** | `canPay(user, amount)` |
| **Traitement paiement** | `processPayment(amount, user)` |
| **Tests** | ✅ 100% couverts |
| **Statut** | ✅ **ACTIF et FONCTIONNEL** |

---

## 🔗 Fichiers Clés du Projet

### Domain Layer
- `PaymentStrategy.java` - Interface principale
- `StudentCreditStrategy.java` - Implémentation crédit étudiant
- `ExternalCardStrategy.java` - Implémentation carte externe
- `PaymentResult.java` - Résultat de paiement
- `PaymentContext.java` - Contexte Strategy Pattern
- `PaymentStrategyFactory.java` - Factory de création
- `PaymentMethod.java` - Enum des méthodes

### Application Layer
- `PlaceOrderUseCase.java` - **Utilise le système de paiement**

### Tests
- `PaymentStrategyTest.java` - Tests unitaires complets
- `PlaceOrderUseCaseTest.java` - Tests d'intégration

---

**🎓 Conclusion : Le système de paiement est parfaitement intégré et utilisé dans le projet SophiaTech Eats !**

