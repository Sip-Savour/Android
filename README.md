# Sip & Savour

Une application Android élégante pour découvrir des accords mets et vins personnalisés.

## À propos

**Sip & Savour** est une application mobile qui vous aide à trouver le vin parfait pour accompagner vos repas, ou inversement, des plats délicieux pour sublimer vos vins préférés.

### Fonctionnalités principales

- **Sélection par couleur** : Rouge, Blanc ou Rosé
- **Profils aromatiques** : Fruité, Floral, Épicé
- **Accords mets-vins** : Suggestions de plats adaptés à chaque vin
- **Favoris** : Sauvegardez vos vins préférés
- **Découverte aléatoire** : Secouez votre téléphone pour une suggestion surprise
- **Suggestion hebdomadaire** : Un nouveau vin chaque semaine
- **Mode sombre** : Interface adaptée jour/nuit
- **Multilingue** : Français et Anglais

## Architecture

### Structure du projet

```
com.sipandsavour/
├── data/                    # Couche de données
│   ├── api/                 # Clients API (Wine, Meal, Auth)
│   ├── dto/                 # Objets de transfert de données
│   ├── Repository           # Gestionnaire de données centralisé
│   └── SessionManager       # Gestion de session utilisateur
│
├── ui/                      # Interface utilisateur
│   ├── auth/                # Authentification (Login/Register)
│   ├── home/                # Écran d'accueil et splash
│   ├── selection/           # Processus de sélection (Couleur/Arômes/Plats)
│   ├── result/              # Résultats et suggestions
│   ├── favorites/           # Gestion des favoris
│   ├── profile/             # Profil, historique, préférences
│   └── common/              # Composants partagés (BaseFragment, UiState)
│
├── util/                    # Utilitaires
│   ├── translation/         # Traduction des ingrédients
│   ├── HapticUtil           # Retours haptiques
│   ├── ShakeDetector        # Détection de secousses
│   └── NetworkUtils         # Gestion réseau
│
├── logic/                   # Logique métier
│   └── FlavorMapper         # Mapping arômes vers vins
│
├── service/                 # Services externes
│   └── LibreTranslateService
│
└── worker/                  # Tâches en arrière-plan
    └── DailySuggestionWorker
```

### Pattern architectural

- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** pour l'abstraction des données
- **LiveData** pour la réactivité
- **Navigation Component** pour la navigation

## Design

### Palette de couleurs

```xml
<!-- Couleurs principales -->
primary:        #8B4E73  (Bordeaux élégant)
secondary:      #D4AF37  (Or)
background:     #F8F6F3  (Crème)
surface:        #FFFFFF

<!-- Couleurs de vin -->
wine_red:       #722F37
wine_white:     #F4E8D0
wine_rose:      #E8B4BC
```

### Typographie

| Usage | Police | Style |
|-------|--------|-------|
| Titres | Playfair Display | Serif élégant |
| Corps | Lato | Sans-serif moderne |

### Thème visuel

- Gradients doux et luxueux
- Ornements dorés
- Cartes avec ombres et coins arrondis
- Animations fluides et retours haptiques

## Technologies

### Stack technique

| Élément | Version |
|---------|---------|
| Langage | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |

### Bibliothèques principales

```gradle
// Architecture
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'
implementation 'androidx.navigation:navigation-fragment:2.7.5'

// UI
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'com.facebook.shimmer:shimmer:0.5.0'

// Network
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Background tasks
implementation 'androidx.work:work-runtime:2.9.0'

// Image loading
implementation 'com.squareup.picasso:picasso:2.8'
```

### APIs externes

**Wine API** (Backend personnalisé)
- Authentification
- Catalogue de vins
- Gestion des favoris
- Prédictions ML

**TheMealDB API**
- Recherche de plats
- Catégories de recettes
- Ingrédients détaillés

**LibreTranslate API**
- Traduction EN vers FR
- Traduction des recettes

## Installation

### Prérequis

- Android Studio Hedgehog (2023.1.1) ou supérieur
- JDK 17
- Un appareil Android ou émulateur (API 24+)

### Configuration

1. Cloner le repository

```bash
git clone https://github.com/votre-username/sip-and-savour.git
cd sip-and-savour
```

2. Configurer les URLs d'API dans `local.properties`

```properties
WINE_API_BASE_URL=https://votre-api-vin.com/
MEAL_API_BASE_URL=https://www.themealdb.com/api/json/v1/1/
TRANSLATE_API_URL=https://libretranslate.com/
```

3. Synchroniser Gradle

```bash
./gradlew sync
```

4. Lancer l'application

```bash
./gradlew installDebug
```

## Guide d'utilisation

### Flux utilisateur principal

**Authentification**

Créer un compte ou se connecter. Les favoris sont synchronisés avec le serveur.

**Sélection**

1. Choisir une couleur de vin (Rouge/Blanc/Rosé)
2. Sélectionner des arômes préférés
3. Optionnel : choisir un plat pour l'accord

**Résultat**

- Découvrir le vin recommandé
- Voir l'accord mets-vin suggéré
- Ajouter aux favoris

**Découverte**

- Suggestion hebdomadaire
- Secouer le téléphone pour un vin aléatoire
- Parcourir l'historique

### Fonctionnalités cachées

- Taper 7 fois sur l'icône de profil pour accéder au mode secret
- Secouer le téléphone pour obtenir un vin mystère

## Tests

```bash
# Tests unitaires
./gradlew test

# Tests instrumentés
./gradlew connectedAndroidTest

# Rapport de couverture
./gradlew jacocoTestReport
```

## Build de production

```bash
# Build release
./gradlew assembleRelease

# Bundle AAB pour Play Store
./gradlew bundleRelease
```

Le fichier APK sera généré dans `app/build/outputs/apk/release/`

## Sécurité

- Stockage sécurisé des tokens (SharedPreferences chiffrées)
- Communication HTTPS uniquement
- Validation des entrées utilisateur
- Gestion des permissions Android

## Internationalisation

Langues supportées :

| Langue | Fichier |
|--------|---------|
| Français (défaut) | `res/values/strings.xml` |
| Anglais | `res/values-en/strings.xml` |

## Compatibilité

- Android 7.0 et supérieur (API 24+)
- Téléphones et tablettes
- Orientations portrait et paysage
- Mode sombre automatique

## Contribution

Les contributions sont les bienvenues.

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

### Standards de code

- Suivre les conventions Java Android
- Commenter le code complexe
- Tester les nouvelles fonctionnalités
- Respecter l'architecture MVVM

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.