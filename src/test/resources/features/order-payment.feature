# language: fr
Fonctionnalité: Passer une commande avec choix de méthode de paiement
  En tant qu'utilisateur du campus
  Je veux pouvoir choisir ma méthode de paiement lors de la validation de ma commande
  Afin de payer selon mes préférences et mes moyens disponibles

  Contexte:
    Étant donné que le système a des restaurants avec des plats
    Et que je suis un utilisateur enregistré nommé "Marie Dupont"
    Et que j'ai un crédit étudiant de 50.00 euros
    Et que mon panier contient les plats suivants:
      | Plat            | Quantité | Prix Unitaire |
      | Pizza Margherita| 1        | 12.50         |
      | Tiramisu        | 1        | 6.50          |

  @payment @student-credit @happy-path
  Scénario: Payer avec crédit étudiant quand le solde est suffisant
    Étant donné que le total de mon panier est de 19.00 euros
    Et que mon crédit étudiant est de 50.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande devrait être créée avec succès
    Et le paiement devrait être effectué avec "CREDIT_ETUDIANT"
    Et mon crédit étudiant devrait être de 31.00 euros
    Et je devrais recevoir un identifiant de transaction commençant par "STU-"
    Et mon panier devrait être vide

  @payment @student-credit @error
  Scénario: Refuser le paiement avec crédit étudiant insuffisant
    Étant donné que mon crédit étudiant est de 10.00 euros
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande ne devrait pas être créée
    Et je devrais recevoir une erreur "Crédit étudiant insuffisant"
    Et le message devrait contenir "Requis: 19.00€, Disponible: 10.00€"
    Et mon crédit étudiant devrait rester à 10.00 euros
    Et mon panier devrait toujours contenir 2 articles

  @payment @external-card @happy-path
  Scénario: Payer avec carte bancaire externe
    Étant donné que le total de mon panier est de 19.00 euros
    Et que mon crédit étudiant est de 10.00 euros
    Quand je choisis de payer avec "CARTE_BANCAIRE"
    Et que je valide ma commande
    Alors la commande devrait être créée avec succès
    Et le paiement devrait être effectué avec "CARTE_BANCAIRE"
    Et mon crédit étudiant devrait rester à 10.00 euros
    Et je devrais recevoir un identifiant de transaction commençant par "EXT-"
    Et mon panier devrait être vide

  @payment @user-choice
  Scénario: L'utilisateur choisit la carte bancaire même avec crédit suffisant
    Étant donné que mon crédit étudiant est de 100.00 euros
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CARTE_BANCAIRE"
    Et que je valide ma commande
    Alors la commande devrait être créée avec succès
    Et le paiement devrait être effectué avec "CARTE_BANCAIRE"
    Et mon crédit étudiant devrait rester à 100.00 euros
    Et le message devrait confirmer "Paiement effectué avec succès par carte bancaire"

  @payment @validation @error
  Scénario: Refuser une commande sans méthode de paiement
    Étant donné que le total de mon panier est de 19.00 euros
    Quand je valide ma commande sans choisir de méthode de paiement
    Alors la commande ne devrait pas être créée
    Et je devrais recevoir une erreur "Invalid request"

  @payment @validation @error
  Scénario: Refuser une commande avec un panier vide
    Étant donné que mon panier est vide
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande ne devrait pas être créée
    Et je devrais recevoir une erreur "Le panier est vide"

  @payment @student-credit
  Scénario: Payer le montant exact avec le crédit étudiant
    Étant donné que mon crédit étudiant est de 19.00 euros
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande devrait être créée avec succès
    Et mon crédit étudiant devrait être de 0.00 euros

  @payment @business-rule
  Scénario: Impossible de passer deux commandes en parallèle
    Étant donné que j'ai déjà une commande en cours
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande ne devrait pas être créée
    Et je devrais recevoir une erreur "Vous avez déjà une commande en cours"

  @payment @external-card @simulation
  Scénario: Simulation d'échec de paiement externe
    Étant donné que le service de paiement externe est indisponible
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CARTE_BANCAIRE"
    Et que je valide ma commande
    Alors la commande ne devrait pas être créée
    Et je devrais recevoir une erreur "Échec du paiement"

  @payment @multiple-items
  Scénario: Payer une grosse commande avec crédit étudiant
    Étant donné que mon panier contient les plats suivants:
      | Plat              | Quantité | Prix Unitaire |
      | Burger XXL        | 2        | 15.00         |
      | Frites Large      | 2        | 4.50          |
      | Coca-Cola         | 2        | 3.00          |
      | Sundae Chocolat   | 2        | 5.00          |
    Et que le total de mon panier est de 55.00 euros
    Et que mon crédit étudiant est de 60.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la commande devrait être créée avec succès
    Et mon crédit étudiant devrait être de 5.00 euros
    Et la commande devrait contenir 4 types de plats différents

  @payment @integration
  Plan du Scénario: Différents montants avec différentes méthodes de paiement
    Étant donné que mon crédit étudiant est de <Crédit> euros
    Et que le total de mon panier est de <Montant> euros
    Quand je choisis de payer avec "<Méthode>"
    Et que je valide ma commande
    Alors le résultat devrait être "<Résultat>"
    Et mon crédit final devrait être de <CréditFinal> euros

    Exemples:
      | Crédit | Montant | Méthode          | Résultat | CréditFinal |
      | 50.00  | 20.00   | CREDIT_ETUDIANT  | succès   | 30.00       |
      | 15.00  | 20.00   | CREDIT_ETUDIANT  | échec    | 15.00       |
      | 15.00  | 20.00   | CARTE_BANCAIRE   | succès   | 15.00       |
      | 100.00 | 99.99   | CREDIT_ETUDIANT  | succès   | 0.01        |
      | 0.00   | 10.00   | CREDIT_ETUDIANT  | échec    | 0.00        |
      | 0.00   | 10.00   | CARTE_BANCAIRE   | succès   | 0.00        |

  @payment @transaction-id
  Scénario: Vérifier les formats d'identifiants de transaction
    Étant donné que mon crédit étudiant est de 50.00 euros
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors l'identifiant de transaction devrait correspondre au format "STU-[A-Z0-9]{8}"
    Et l'identifiant devrait être unique

  @payment @response-validation
  Scénario: Vérifier la réponse complète après paiement réussi
    Étant donné que mon crédit étudiant est de 50.00 euros
    Et que le total de mon panier est de 19.00 euros
    Quand je choisis de payer avec "CREDIT_ETUDIANT"
    Et que je valide ma commande
    Alors la réponse devrait contenir:
      | Champ              | Valeur attendue           |
      | customerName       | Marie Dupont              |
      | totalAmount        | 19.00                     |
      | status             | PENDING                   |
      | paymentMethod      | STUDENT_CREDIT            |
    Et la réponse devrait contenir un orderId non vide
    Et la réponse devrait contenir un orderDateTime valide

