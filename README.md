# Sip & Savour

Le vin parfait pour chaque plat

## Description

Sip & Savour est une application Android innovante qui recommande des vins en fonction de vos plats préférés. En analysant les saveurs, les ingrédients et les préférences culinaires, l'app vous aide à trouver l'accord vin-plat idéal pour enrichir vos expériences gastronomiques.

## Fonctionnalités

### 🍷 Recommandations Personnalisées
- **Sélection Alimentaire** : Choisissez votre catégorie de plat (viande, poisson, végétarien, fromage, etc.)
- **Sous-Catégories Détaillées** : Précisez votre plat (bœuf, agneau, poisson blanc, légumes grillés, etc.)
- **Saveurs** : Sélectionnez les caractéristiques gustatives (grillé, fumé, épicé, crémeux, etc.)
- **Préférence de Couleur** : Rouge, blanc, rosé ou pas de préférence

### 🏠 Accueil
- **Suggestion du Jour** : Découvrez une recommandation quotidienne
- **Sélection Hebdomadaire** : Explorez des vins sélectionnés par nos experts
- **Recherche Rapide** : Accédez facilement à la fonction de recherche

### ⭐ Favoris
- Sauvegardez vos vins préférés
- Gérez facilement votre liste personnelle

### 👤 Profil
- Gérez votre compte utilisateur
- Personnalisez vos préférences

### 🔐 Authentification
- Connexion et inscription sécurisées
- Gestion de session persistante

## Technologies Utilisées

- **Langage** : Java
- **Architecture** : MVVM avec Repository Pattern
- **Injection de Dépendances** : Hilt (Dagger)
- **Base de Données** : Room
- **API** : Retrofit avec OkHttp
- **Navigation** : Navigation Component
- **Images** : Glide
- **Animations** : Lottie
- **Stockage** : DataStore Preferences
- **Tâches en Arrière-Plan** : WorkManager

## Installation

### Prérequis
- Android Studio Arctic Fox ou supérieur
- JDK 17
- Android SDK API 36
- Minimum SDK 26 (Android 8.0)

### Configuration
1. Clonez le repository :
   ```bash
   git clone https://github.com/votre-repo/sip-savour.git
   ```

2. Ouvrez le projet dans Android Studio

3. Synchronisez les dépendances Gradle

4. Pour le développement local :
   - Assurez-vous que votre serveur backend fonctionne sur `http://10.0.2.2:8000/`
   - Pour la production, l'API pointe vers `https://api.sipandsavour.com/`

5. Lancez l'application sur un émulateur ou un appareil

## Architecture

L'application suit une architecture moderne Android :

- **UI Layer** : Fragments avec View Binding
- **ViewModel** : Gestion de l'état et logique métier
- **Repository** : Abstraction des sources de données
- **Data Layer** : API REST et base de données locale
- **Dependency Injection** : Hilt pour l'injection de dépendances

## API

L'application communique avec un backend REST API pour :
- Authentification utilisateur
- Recommandations de vins basées sur les critères alimentaires
- Gestion des favoris
- Suggestions quotidiennes/hebdomadaires

## Permissions

L'application nécessite les permissions suivantes :
- Internet : Pour les appels API
- État du réseau : Pour vérifier la connectivité
- Vibration : Pour les feedbacks utilisateur
- Enregistrement audio : Pour les fonctionnalités futures

## Contribution

Les contributions sont les bienvenues ! Veuillez suivre ces étapes :

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Pushez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.
