# 💳 Choix de la Méthode de Paiement par l'Utilisateur

## 🎯 Principe Fondamental

**C'EST L'UTILISATEUR QUI CHOISIT** sa méthode de paiement, **PAS LE SYSTÈME** !

Le système propose les options disponibles, mais c'est toujours l'utilisateur qui décide.

---

## 🔄 Flux Complet du Choix Utilisateur

### 📍 Scénario : Marie veut commander pour 25€

```
Crédit étudiant de Marie : 10€
Commande : Big King XXL (25€)
```

---

## ÉTAPE 1️⃣ : Affichage des Options (Front-End)

### Le Front-End récupère d'abord les informations de l'utilisateur

```http
GET /api/users/marie-001
```

**Réponse :**
```json
{
  "userId": "marie-001",
  "name": "Marie Dupont",
  "email": "marie@polytech.fr",
  "studentCredit": 10.00,
  "hasExternalCard": true
}
```

### Le Front-End affiche le panier avec le total

```
🛒 Votre Panier
─────────────────────────────
Big King XXL x1         25.00€
─────────────────────────────
Total à payer:          25.00€

[Passer la commande →]
```

---

## ÉTAPE 2️⃣ : Marie Clique sur "Passer la commande"

### Le Front-End affiche une MODALE de choix de paiement

```
┌─────────────────────────────────────────┐
│  💰 Comment souhaitez-vous payer ?      │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎓 Crédit Étudiant              │   │
│  │                                 │   │
│  │ Solde disponible: 10.00€        │   │
│  │ Montant requis:   25.00€        │   │
│  │                                 │   │
│  │ ❌ INSUFFISANT                  │   │
│  │                                 │   │
│  │ [○] Sélectionner                │   │
│  │     (Désactivé)                 │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 💳 Carte Bancaire Visa          │   │
│  │                                 │   │
│  │ **** **** **** 1234             │   │
│  │                                 │   │
│  │ ✅ DISPONIBLE                   │   │
│  │                                 │   │
│  │ [●] Sélectionner ← Sélectionné  │   │
│  └─────────────────────────────────┘   │
│                                         │
├─────────────────────────────────────────┤
│  Total à payer: 25.00€                  │
│                                         │
│  [Annuler]          [Confirmer →]       │
└─────────────────────────────────────────┘
```

### Code Front-End (React/Vue/Angular exemple)

```javascript
function PaymentMethodSelector({ user, totalAmount }) {
  const [selectedMethod, setSelectedMethod] = useState(null);
  
  // Vérifier si le crédit étudiant est suffisant
  const canUseStudentCredit = user.studentCredit >= totalAmount;
  
  return (
    <div className="payment-methods">
      <h3>Comment souhaitez-vous payer ?</h3>
      
      {/* Option 1 : Crédit Étudiant */}
      <div className={`payment-option ${!canUseStudentCredit ? 'disabled' : ''}`}>
        <input 
          type="radio" 
          name="paymentMethod" 
          value="STUDENT_CREDIT"
          disabled={!canUseStudentCredit}
          checked={selectedMethod === 'STUDENT_CREDIT'}
          onChange={() => setSelectedMethod('STUDENT_CREDIT')}
        />
        <label>
          <h4>🎓 Crédit Étudiant</h4>
          <p>Solde disponible: {user.studentCredit}€</p>
          <p>Montant requis: {totalAmount}€</p>
          {!canUseStudentCredit && (
            <span className="error">❌ Crédit insuffisant</span>
          )}
          {canUseStudentCredit && (
            <span className="success">✅ Disponible</span>
          )}
        </label>
      </div>
      
      {/* Option 2 : Carte Bancaire */}
      <div className="payment-option">
        <input 
          type="radio" 
          name="paymentMethod" 
          value="EXTERNAL_CARD"
          checked={selectedMethod === 'EXTERNAL_CARD'}
          onChange={() => setSelectedMethod('EXTERNAL_CARD')}
        />
        <label>
          <h4>💳 Carte Bancaire</h4>
          <p>**** **** **** 1234</p>
          <span className="success">✅ Disponible</span>
        </label>
      </div>
      
      {/* Bouton de confirmation */}
      <button 
        disabled={!selectedMethod}
        onClick={() => handlePlaceOrder(selectedMethod)}
      >
        Confirmer le paiement
      </button>
    </div>
  );
}
```

---

## ÉTAPE 3️⃣ : Marie CHOISIT "Carte Bancaire"

### Marie clique sur le bouton radio "Carte Bancaire"

```javascript
// État mis à jour dans le front-end
selectedMethod = "EXTERNAL_CARD"
```

**Interface mise à jour :**
```
┌─────────────────────────────────────────┐
│  💰 Comment souhaitez-vous payer ?      │
├─────────────────────────────────────────┤
│                                         │
│  [○] 🎓 Crédit Étudiant (Insuffisant)   │
│                                         │
│  [●] 💳 Carte Bancaire ← SÉLECTIONNÉ ✅ │
│                                         │
├─────────────────────────────────────────┤
│  Total à payer: 25.00€                  │
│                                         │
│  [Annuler]          [Confirmer →]       │
└─────────────────────────────────────────┘
```

---

## ÉTAPE 4️⃣ : Marie Clique sur "Confirmer"

### Le Front-End envoie la requête avec le CHOIX de Marie

```javascript
async function handlePlaceOrder(paymentMethod) {
  const request = {
    userId: "marie-001",
    restaurantId: "123",
    paymentMethod: paymentMethod  // ← LE CHOIX DE MARIE !
  };
  
  try {
    const response = await fetch('/api/orders/place', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userToken}`
      },
      body: JSON.stringify(request)
    });
    
    if (response.ok) {
      const order = await response.json();
      showSuccessMessage(order);
    } else {
      const error = await response.json();
      showErrorMessage(error.message);
    }
  } catch (error) {
    console.error('Erreur lors du paiement:', error);
  }
}
```

### Requête HTTP envoyée

```http
POST /api/orders/place HTTP/1.1
Host: api.sophiatecheats.fr
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "userId": "marie-001",
  "restaurantId": "123",
  "paymentMethod": "EXTERNAL_CARD"  ← LE CHOIX DE MARIE !
}
```

---

## ÉTAPE 5️⃣ : Le Back-End Reçoit le Choix de Marie

### Dans PlaceOrderUseCase.java

```java
@Override
public PlaceOrderResponse execute(PlaceOrderRequest request) {
    // request.paymentMethod() contient "EXTERNAL_CARD"
    // C'est le choix que Marie a fait dans le front-end !
    
    // ... validation et récupération des données ...
    
    // Ligne 108 : Le système crée le contexte SELON LE CHOIX DE MARIE
    PaymentContext paymentContext = new PaymentContext(request.paymentMethod());
    //                                                  ↑
    //                                    C'est "EXTERNAL_CARD" choisi par Marie
    
    // Le PaymentContext sélectionne automatiquement la stratégie correspondante
    // EXTERNAL_CARD → ExternalCardStrategy
    // STUDENT_CREDIT → StudentCreditStrategy
    
    // Ligne 111 : Vérification avec la stratégie choisie
    if (!paymentContext.canUserPay(user, totalAmount)) {
        throw new InsufficientCreditException("...");
    }
    
    // Ligne 119 : Paiement avec la stratégie choisie
    PaymentResult paymentResult = paymentContext.executePayment(totalAmount, user);
    
    // ... reste du code ...
}
```

---

## 📊 Diagramme du Flux de Décision

```
Marie (Utilisateur)
    ↓
    ├─ Affichage du panier (25€)
    ↓
    ├─ Clic sur "Passer commande"
    ↓
    ├─ CHOIX de Marie :
    │  ┌─────────────────────────────┐
    │  │ ○ Crédit Étudiant (10€)     │ ← Insuffisant, désactivé
    │  │   ❌ Insuffisant            │
    │  │                             │
    │  │ ● Carte Bancaire            │ ← Marie CHOISIT celle-ci ✅
    │  │   ✅ Disponible             │
    │  └─────────────────────────────┘
    ↓
    ├─ Marie clique "Confirmer"
    ↓
Front-End envoie :
    {
      "paymentMethod": "EXTERNAL_CARD"  ← CHOIX de Marie
    }
    ↓
Back-End reçoit le choix
    ↓
PlaceOrderUseCase crée :
    PaymentContext(EXTERNAL_CARD)  ← Utilise le CHOIX de Marie
    ↓
PaymentContext sélectionne :
    ExternalCardStrategy  ← Stratégie correspondant au CHOIX de Marie
    ↓
Paiement traité avec la carte bancaire
    ↓
Commande créée avec :
    paymentMethod = EXTERNAL_CARD
```

---

## 🎭 Scénario Alternatif : Si Marie avait 50€ de crédit

### Affichage avec crédit suffisant

```
┌─────────────────────────────────────────┐
│  💰 Comment souhaitez-vous payer ?      │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎓 Crédit Étudiant              │   │
│  │                                 │   │
│  │ Solde disponible: 50.00€        │   │
│  │ Montant requis:   25.00€        │   │
│  │                                 │   │
│  │ ✅ DISPONIBLE                   │   │
│  │                                 │   │
│  │ [●] Sélectionner ← Choix par défaut │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 💳 Carte Bancaire               │   │
│  │                                 │   │
│  │ **** **** **** 1234             │   │
│  │                                 │   │
│  │ ✅ DISPONIBLE                   │   │
│  │                                 │   │
│  │ [○] Sélectionner                │   │
│  └─────────────────────────────────┘   │
│                                         │
├─────────────────────────────────────────┤
│  💡 Conseil : Utilisez votre crédit     │
│     étudiant pour économiser !          │
├─────────────────────────────────────────┤
│  Total à payer: 25.00€                  │
│                                         │
│  [Annuler]          [Confirmer →]       │
└─────────────────────────────────────────┘
```

### Marie peut choisir entre les DEUX options

```javascript
// Marie CHOISIT le crédit étudiant
request = {
  "userId": "marie-001",
  "restaurantId": "123",
  "paymentMethod": "STUDENT_CREDIT"  ← Son choix
}

// Back-End utilisera StudentCreditStrategy
// Le crédit de Marie passera de 50€ à 25€
```

**OU**

```javascript
// Marie CHOISIT la carte bancaire (même avec du crédit disponible)
request = {
  "userId": "marie-001",
  "restaurantId": "123",
  "paymentMethod": "EXTERNAL_CARD"  ← Son choix
}

// Back-End utilisera ExternalCardStrategy
// Le crédit de Marie reste à 50€ (inchangé)
```

---

## 🔑 Points Clés à Retenir

### ✅ Ce qui est VRAI

1. **L'utilisateur CHOISIT** sa méthode de paiement dans le front-end
2. Le choix est envoyé dans `PlaceOrderRequest.paymentMethod`
3. Le système **respecte toujours** le choix de l'utilisateur
4. Le `PaymentContext` sélectionne automatiquement la **stratégie correspondante**

### ❌ Ce qui est FAUX

1. ❌ Le système ne décide PAS à la place de l'utilisateur
2. ❌ Le système ne force PAS une méthode de paiement
3. ❌ Le système ne change PAS le choix de l'utilisateur

### ⚠️ Validations du Système

Le système fait quand même des **validations** :

```java
// Si l'utilisateur choisit STUDENT_CREDIT mais n'a pas assez de crédit
if (!paymentContext.canUserPay(user, totalAmount)) {
    throw new InsufficientCreditException(
        "Crédit insuffisant pour cette méthode de paiement"
    );
    // La commande est REFUSÉE
}
```

**Mais** : Le front-end devrait déjà empêcher ce cas en désactivant l'option !

---

## 🛡️ Sécurité : Validation Côté Serveur

### Le Front-End peut mentir !

Un utilisateur malveillant pourrait modifier la requête :

```javascript
// Tentative de fraude : Marie envoie STUDENT_CREDIT alors qu'elle n'a que 10€
fetch('/api/orders/place', {
  body: JSON.stringify({
    userId: "marie-001",
    restaurantId: "123",
    paymentMethod: "STUDENT_CREDIT"  // ← Modification malveillante
  })
});
```

### Le Back-End vérifie TOUJOURS

```java
// Ligne 111 dans PlaceOrderUseCase
if (!paymentContext.canUserPay(user, totalAmount)) {
    throw new InsufficientCreditException(
        "Paiement impossible avec crédit étudiant. Montant requis: 25.00€, Crédit disponible: 10.00€"
    );
}

// ❌ La commande est REFUSÉE
// Réponse HTTP 400 Bad Request
```

**Résultat :** La fraude est bloquée ! ✅

---

## 📋 Récapitulatif du Flux Complet

```
1. Marie voit son panier (25€)
   ↓
2. Marie clique "Passer commande"
   ↓
3. Front-End affiche les OPTIONS disponibles :
   - Crédit Étudiant (10€) ❌ Insuffisant → Option désactivée
   - Carte Bancaire ✅ Disponible → Option active
   ↓
4. MARIE CHOISIT "Carte Bancaire"
   ↓
5. Marie clique "Confirmer"
   ↓
6. Front-End envoie POST /api/orders/place avec :
   {
     "userId": "marie-001",
     "restaurantId": "123",
     "paymentMethod": "EXTERNAL_CARD"  ← LE CHOIX DE MARIE
   }
   ↓
7. Back-End reçoit la requête
   ↓
8. PlaceOrderUseCase exécute :
   PaymentContext context = new PaymentContext(request.paymentMethod());
   // request.paymentMethod() = "EXTERNAL_CARD" (choix de Marie)
   ↓
9. PaymentContext sélectionne ExternalCardStrategy
   ↓
10. Vérification : context.canUserPay(marie, 25€) → true ✅
    ↓
11. Paiement : context.executePayment(25€, marie)
    ↓
12. ExternalCardStrategy.processPayment() s'exécute
    ↓
13. Paiement réussi → Transaction ID: EXT-A7F9C2E1
    ↓
14. Commande créée avec paymentMethod = EXTERNAL_CARD
    ↓
15. Réponse envoyée au front-end
    ↓
16. Marie voit la confirmation :
    "✅ Commande confirmée ! Paiement par carte bancaire réussi."
```

---

## 🎯 Conclusion

### Le rôle de chacun

| Acteur | Responsabilité |
|--------|----------------|
| **👤 Utilisateur (Marie)** | **CHOISIT** la méthode de paiement |
| **🖥️ Front-End** | Affiche les options, désactive les options impossibles |
| **⚙️ Back-End** | **RESPECTE** le choix, **VALIDE** la faisabilité, **EXÉCUTE** le paiement |
| **🎨 PaymentContext** | Sélectionne la **stratégie** correspondant au choix |
| **💳 Strategy** | Traite le paiement selon la méthode choisie |

### C'est un système démocratique ! 🗳️

- L'utilisateur vote (choisit)
- Le système compte le vote (reçoit le choix)
- Le système exécute la volonté de l'utilisateur (applique la stratégie)
- Le système vérifie la légalité (validation)

**L'utilisateur a le pouvoir de décision, le système a le pouvoir d'exécution et de validation !** ✅

---

## 📚 Fichiers Concernés

1. **Front-End** (à créer) :
   - `PaymentMethodSelector.jsx` - Composant de sélection
   - `CheckoutPage.jsx` - Page de validation de commande

2. **Back-End** (existants) :
   - `PlaceOrderRequest.java` - Reçoit le choix (`paymentMethod`)
   - `PlaceOrderUseCase.java` - Utilise le choix (ligne 108)
   - `PaymentContext.java` - Crée la stratégie selon le choix
   - `PaymentStrategyFactory.java` - Fabrique la stratégie
   - `StudentCreditStrategy.java` - Si choix = STUDENT_CREDIT
   - `ExternalCardStrategy.java` - Si choix = EXTERNAL_CARD

---

**🎓 L'utilisateur est roi ! C'est lui qui décide comment il veut payer !** 👑

