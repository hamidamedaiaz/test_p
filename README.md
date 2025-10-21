# SophiaTech Eats 

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-green)
![Tests](https://img.shields.io/badge/Tests-231%20Passing-brightgreen)

**Système de commande de repas pour le campus universitaire de Sophia Antipolis**

Application de restauration universitaire permettant aux étudiants de commander des repas avec paiement par crédit étudiant et gestion des créneaux de livraison.

##  Table des Matières

- [Vue d'ensemble](#vue-densemble)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Installation et Utilisation](#installation-et-utilisation)
- [Parcours Utilisateur Complet](#parcours-utilisateur-complet)
- [Spécifications Métier](#spécifications-métier)
- [Analyse de Conformité](#analyse-de-conformité)
- [Structure du Projet](#structure-du-projet)
- [Tests](#tests)
- [Développement](#développement)

##  Vue d'ensemble

SophiaTech Eats est une application de commande de repas conçue spécifiquement pour l'écosystème universitaire. Elle permet aux étudiants de :

- **Parcourir** les restaurants et plats du campus
- **Commander** avec leur crédit étudiant
- **Choisir** des créneaux de livraison
- **Gérer** leur panier en temps réel

### Caractéristiques Principales

- **Java pur** (pas de Spring) pour un apprentissage pédagogique approfondi
- **Clean Architecture** avec séparation stricte des couches
- **Paiement par crédit étudiant** intégré
- **Gestion des créneaux de livraison** par capacité
- **Tests complets** (unitaires, intégration, Cucumber)

##  Fonctionnalités

###  Gestion des Restaurants
- **Navigation intuitive** : Parcours des restaurants du campus
- **Filtrage avancé** : Par statut (ouvert/fermé), type de cuisine, catégories
- **Menus détaillés** : Affichage des plats avec prix et descriptions
- **Horaires dynamiques** : Respect des heures d'ouverture/fermeture

###   Gestion du Panier
- **Ajout intelligent** : Validation des quantités (max 10/plat)
- **Modification en temps réel** : Mise à jour des quantités
- **Règles métier** : Un seul restaurant par panier, limite 100€
- **Persistance** : Sauvegarde avec expiration automatique (24h)

###  Gestion des Livraisons
- **Créneaux de 30 minutes** avec capacités définies
- **Réservation temps réel** : Gestion de la concurrence
- **Estimation précise** : Temps de livraison = fin créneau + 15min
- **Validation dynamique** : Vérification de disponibilité

###  Système de Paiement
- **Crédit étudiant** : Gestion complète des soldes
- **Validation automatique** : Vérification de fonds suffisants
- **Déduction sécurisée** : Transaction atomique
- **Traçabilité** : Historique des paiements

###  Gestion des Utilisateurs
- **Identification simple** : Pas d'authentification complexe
- **Profil étudiant** : Nom, email, crédit disponible
- **Gestion du crédit** : Ajout, déduction, consultation

##  Architecture

Le projet suit les principes de **Clean Architecture** avec une séparation claire des responsabilités :

```
├── domain/           #  Logique métier pure
│   ├── entities/     # Objets métier (User, Restaurant, Order...)
│   ├── repositories/ # Interfaces de persistance
│   ├── services/     # Services métier
│   └── exceptions/   # Exceptions métier
│
├── application/      #  Orchestration des workflows
│   ├── usecases/     # Cas d'utilisation métier
│   ├── dto/          # Objets de transfert
│   └── ports/        # Interfaces d'adaptation
│
└── infrastructure/   #  Détails techniques
    ├── repositories/ # Implémentations (mémoire)
    └── config/       # Configuration DI (Picocontainer)
```

### Flux de Dépendances
```
Infrastructure → Application → Domain
```

##  Installation et Utilisation

### Prérequis
- **Java 21** ou supérieur
- **Maven 3.9+**

### Installation
```bash
git clone <repository-url>
cd ste-25-26-team-p-1
mvn clean compile
```

### Commandes Utiles
```bash
# Compilation
mvn clean compile

# Tests (tous)
mvn test

# Tests spécifique
mvn -Dtest=RestaurantTest test

# Exécution de l'application
mvn exec:java -Dexec.mainClass="fr.unice.polytech.sophiatecheats.SophiaTechEatsApplication"

# Test d'un use case
mvn -Dtest=BrowseRestaurantsUseCaseTest test
```

## Parcours Utilisateur Complet

Voici comment un étudiant peut commander de bout en bout :

### 1. Navigation des Restaurants
```java
ApplicationConfig config = new ApplicationConfig();
BrowseRestaurantsUseCase browseUseCase = config.getInstance(BrowseRestaurantsUseCase.class);

BrowseRestaurantsResponse restaurants = browseUseCase.execute(
    new BrowseRestaurantsRequest(null, "open") // Restaurants ouverts
);
```

### 2. Ajout au Panier
```java
AddDishToCartUseCase addToCartUseCase = config.getInstance(AddDishToCartUseCase.class);

AddDishToCartResponse cartResponse = addToCartUseCase.execute(
    new AddDishToCartRequest(userId, dishId, 2) // 2 pizzas
);
```

### 3. Sélection du Créneau de Livraison
```java
GetAvailableDeliverySlotsUseCase slotsUseCase = config.getInstance(GetAvailableDeliverySlotsUseCase.class);

List<DeliverySlotDTO> availableSlots = slotsUseCase.execute(LocalDate.now());

SelectDeliverySlotUseCase selectUseCase = config.getInstance(SelectDeliverySlotUseCase.class);
selectUseCase.execute(slotId, userId);
```

### 4. Validation et Paiement
```java
PlaceOrderUseCase placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);

PlaceOrderResponse order = placeOrderUseCase.execute(new PlaceOrderRequest(
    userId,
    restaurantId,
        List.of(new OrderItemRequest(dishId, 2)),
    PaymentMethod.STUDENT_CREDIT
));
```

** Commande validée et crédit étudiant débité automatiquement !**

##  Spécifications Métier

### Contraintes Principales

#### Gestion des Créneaux
- **Durée** : Créneaux de 30 minutes fixes
- **Capacité** : Limitée par restaurant (ex: 2 commandes/créneau)
- **Calcul livraison** : Fin créneau + 15 minutes
- **Réservation** : Immédiate lors de la sélection

#### Règles de Commande
- **Un restaurant par commande** : Pas de mélange possible
- **Une commande active** : Pas de commandes parallèles
- **Limite panier** : 100€ maximum
- **Quantité** : 10 plats maximum par type

#### Paiement
- **Crédit étudiant** : Principal moyen de paiement
- **Validation préalable** : Vérification des fonds
- **Pas de combinaison** : Un seul moyen par commande
- **Timeout planifié** : Libération auto des créneaux (à implémenter)

### Simplifications Assumées
- **Pas d'authentification** : Identification par ID simple
- **Temps de préparation nul** : Commandes instantanément prêtes
- **Pas de livraison réelle** : Simulation uniquement
- **Pas de concurrence** : Validation simultanée possible

## Analyse de Conformité

###  Implémenté (77% de conformité globale)

| Fonctionnalité | Statut | Conformité |
|----------------|--------|------------|
| **Gestion Utilisateurs** | ✅ | 90% |
| **Navigation Restaurants** | ✅ | 85% |
| **Gestion Panier** | ✅ | 95% |
| **Créneaux de Livraison** | ✅ | 90% |
| **Paiement Crédit Étudiant** | ✅ | 85% |
| **Architecture Clean** | ✅ | 95% |

### Manquant (priorités d'implémentation)

#### Critique
1. **Service de paiement externe** + mock
2. **Timeout de paiement** avec libération auto des créneaux
3. **Réordonnancement workflow** : Validation → Créneau → Paiement

#### Important
4. **Interface Restaurant** (application dédiée)
5. **Mémorisation payment method** dans Order
6. **Use cases Restaurant** (gestion menu, capacités)

###  Présent mais non demandé
- Historique des commandes avancé
- Gestion fine des catégories de plats
- Infrastructure de notifications

##  Structure du Projet

```
src/
├── main/java/fr/unice/polytech/sophiatecheats/
│   ├── domain/
│   │   ├── entities/
│   │   │   ├── restaurant/     # Restaurant, Dish, TimeSlot, CapacitySlot
│   │   │   ├── cart/           # Cart, CartItem
│   │   │   ├── order/          # Order, OrderItem
│   │   │   ├── user/           # User
│   │   │   └── delivery/       # DeliverySlot, DeliverySchedule
│   │   ├── repositories/       # Interfaces Repository
│   │   ├── services/           # PaymentService, DeliveryService
│   │   ├── enums/              # OrderStatus, PaymentMethod, DishCategory
│   │   └── exceptions/         # Exceptions métier
│   │
│   ├── application/
│   │   ├── usecases/
│   │   │   ├── restaurant/     # BrowseRestaurantsUseCase
│   │   │   ├── cart/           # Add/Remove/Update/Get/ClearCartUseCase
│   │   │   ├── order/          # PlaceOrderUseCase
│   │   │   └── delivery/       # GetAvailableSlots, SelectSlot, ValidateSlot
│   │   ├── dto/                # DTOs pour chaque domaine
│   │   └── ports/              # Interfaces d'adaptation
│   │
│   └── infrastructure/
│       ├── repositories/
│       │   └── memory/         # Implémentations en mémoire
│       └── config/             # ApplicationConfig (DI)
│
└── test/
    ├── java/                   # Tests unitaires et d'intégration
    └── resources/
        └── features/           # Tests Cucumber (BDD)
```

##  Tests

Le projet dispose d'une couverture de test complète :

### Statistiques
- **231 tests** tous passants 
- **Tests unitaires** : Toutes les entités et use cases
- **Tests d'intégration** : Workflows complets
- **Tests Cucumber** : 28 scénarios BDD

### Exécution des Tests
```bash
# Tous les tests
mvn test

# Tests spécifiques par domaine
mvn -Dtest=*RestaurantTest test
mvn -Dtest=*CartTest test
mvn -Dtest=*DeliveryTest test

# Tests Cucumber uniquement
mvn -Dtest=CucumberTest test
```

### Architecture de Test
- **Given-When-Then** : Structure BDD claire
- **Mocks intelligents** : Simulation des dépendances

## Développement

### Patterns Utilisés
- **Repository Pattern** : Abstraction de la persistance
- **Use Case Pattern** : Encapsulation des workflows métier  
- **Builder Pattern** : Construction d'objets complexes
- **Factory Pattern** : Création d'entités (via DI)

### Principes Respectés
- **SOLID** : Single responsibility, Open/Closed, etc.
- **DRY** : Don't Repeat Yourself
- **KISS** : Keep It Simple, Stupid
- **Clean Code** : Nommage explicite, méthodes courtes

### Workflow de Développement
1. **Domain First** : Commencer par les entités métier
2. **Use Cases** : Implémenter les workflows
3. **Tests** : TDD/BDD pour la validation
4. **Infrastructure** : Dernière couche d'adaptation

### Contribution
```bash
# Cloner et setup
git clone <repo>
cd ste-25-26-team-p-1

# Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# Développer avec tests
mvn test  # Vérifier que tout passe

# Committer avec message clair
git commit -m "feat: ajouter nouvelle fonctionnalité X"
```

---

## Contexte Pédagogique

Ce projet est conçu dans un cadre pédagogique pour :
- **Maîtriser Java pur** sans framework magique
- **Comprendre Clean Architecture** en pratique
- **Appliquer les patterns GoF** de manière justifiée
- **Gérer la complexité** d'un système métier réel

L'accent est mis sur la **compréhension profonde** des mécanismes plutôt que sur l'utilisation d'outils abstraits.

---
