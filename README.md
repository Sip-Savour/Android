# 🍷 Sip & Savour

**Le vin parfait pour chaque plat** - Application Android de recommandation de vins intelligents

![Android](https://img.shields.io/badge/Android-36-brightgreen) ![Java](https://img.shields.io/badge/Java-17-orange) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 📱 Vue d'Ensemble

Sip & Savour est une application Android innovante qui recommande des vins basés sur vos préférences culinaires. L'app utilise :

- **L'Analyse Culinaire** : Saveurs, textures et caractéristiques du plat
- **Traitement IA** : Algorithme intelligent d'appairage vin-mets
- **Suggestions Dynamiques** : Cartes hebdomadaires et suggestions quotidiennes
- **Intégration TheMealDB** : Recettes, ingrédients, instructions étape par étape
- **Traduction en Temps Réel** : Recettes traduites EN ↔ FR via Google ML Kit

---

## ✨ Fonctionnalités Principales

### 🏠 Accueil
- Suggestion personnalisée du jour
- Sélection hebdomadaire curée par experts
- Accès direct à la recherche personnalisée
- Design premium avec dégradés Bordeaux & Or

### 🔍 Recherche Intelligente
1. **Sélection Alimentaire** : Viande, Poisson, Végétarien, Fromage, Fruits de mer
2. **Sous-Catégories** : Bœuf, Agneau, Poisson blanc, Pâtes, Risotto, etc.
3. **Saveurs Détaillées** : 
   - Fruits (Rouges, Noirs, Secs, Agrumes, Tropicaux)
   - Structure (Acidité, Tanins, Corps, Texture)
   - Arômes (Bois, Fumée, Épices, Noisette, Cacao)
   - Terroir (Terreux, Minéral, Umami, Complexité)
4. **Couleur** : Rouge, Blanc, Rosé, ou pas de préférence

### 📖 Recettes & Plats
- Suggestions de recettes TheMealDB associées au vin
- Affichage complet : Ingrédients + Instructions étape-par-étape
- **Traduction EN ↔ FR automatique** selon la langue de l'app
- Bottom sheet intuitif pour consulter les détails

### ⭐ Favoris
- Sauvegarde des vins préférés
- Synchronisation avec serveur
- Gestion intuitive (suppression, tri)

### 👤 Profil & Paramètres
- Gestion du compte utilisateur
- Sélection de la langue (FR/EN)
- Historique de consultation
- Déconnexion sécurisée

### 🔐 Authentification
- Inscription et connexion sécurisées
- Gestion de session persistante
- Token JWT pour API REST

---

## 📚 Documentation Complète

### 🔗 Guides Disponibles

| Document | Description |
|----------|-------------|
| **[README_COMPLET.md](README_COMPLET.md)** | Documentation complète (70+ pages) |
| **[INTEGRATION_THEMEALDB.md](INTEGRATION_THEMEALDB.md)** | Guide d'intégration TheMealDB API |
| **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** | Guide de dépannage et erreurs courantes |

---

## 🛠️ Stack Technologique

### Framework & Langage
- **Java 17** + Android Framework (API 36 max, 26 min)
- **MVVM Architecture** avec Repository Pattern
- **Hilt** pour injection de dépendances

### Networking & APIs
- **Retrofit 2** : Client REST
- **OkHttp 4** : Caching, logging, intercepteurs
- **TheMealDB API** : Recettes, ingrédients, instructions

### Traitement Asynchrone
- **LiveData** : Observables réactifs
- **Coroutines** : Programmation réactive
- **WorkManager** : Tâches programmées

### Données & UI
- **Room** : Base de données locale
- **View Binding** : Accès sécurisé aux vues
- **Material Design 3** : Composants modernes
- **Glide** : Gestion d'images
- **Lottie** : Animations

### ML & Traduction
- **Google ML Kit Translation** : Traduction EN ↔ FR sur-appareil
- **Texte à Voix** : Support complet des recettes

---

## 🚀 Installation Rapide

### Prérequis
- Android Studio Arctic Fox+
- JDK 17+
- Android SDK API 36 (target), 26+ (min)

### Étapes

```bash
# 1. Cloner
git clone https://github.com/votre-repo/sip-savour.git
cd Android

# 2. Ouvrir dans Android Studio
# Fichier → Ouvrir → Android

# 3. Synchroniser Gradle (auto)

# 4. Configurer local.properties
# android.sdk.path=/chemin/vers/sdk

# 5. Lancer l'app
# Run → Run 'app'
```

---

## 📂 Architecture du Projet

```
Android/
├── app/src/main/java/com/sipandsavour/
│   ├── data/
│   │   ├── api/          # Clients Retrofit (Backend, TheMealDB)
│   │   ├── dto/          # Modèles de données
│   │   ├── Repository.java
│   │   └── SessionManager.java
│   │
│   ├── ui/
│   │   ├── auth/         # Authentification
│   │   ├── home/         # Accueil
│   │   ├── selection/    # Recherche & saveurs
│   │   ├── result/       # Affichage résultats
│   │   ├── favorites/    # Favoris
│   │   └── profile/      # Profil utilisateur
│   │
│   ├── logic/
│   │   └── FlavorMapper.java  # Mapping saveurs → vins
│   │
│   └── util/
│       ├── MealTranslationManager.java  # Traduction ML Kit
│       ├── TranslationManager.java
│       └── Constants.java
│
├── res/
│   ├── layout/          # XMLs d'interface
│   ├── values/          # Strings FR, couleurs, dimens
│   └── values-en/       # Strings EN
│
└── README.md, README_COMPLET.md, INTEGRATION_THEMEALDB.md, TROUBLESHOOTING.md
```

---

## 🔌 API TheMealDB Intégrée

### Vue d'Ensemble

L'app utilise TheMealDB pour :
- Récupérer les catégories de recettes
- Afficher les recettes associées au vin recommandé
- Afficher les ingrédients complets et les instructions

**Base URL** : `https://www.themealdb.com/api/json/v1/1/`

### Endpoints Utilisés

| Endpoint | Usage |
|----------|-------|
| `GET /categories.php` | Lister les catégories |
| `GET /filter.php?c={cat}` | Filtrer par catégorie |
| `GET /lookup.php?i={id}` | Détails complets recette |
| `GET /search.php?s={name}` | Recherche par nom |
| `GET /random.php` | Recette aléatoire |

### Traduction Automatique

Les recettes TheMealDB sont traduites automatiquement :

```java
MealTranslationManager.getInstance().translateMealIfNeeded(meal, translatedMeal -> {
    // Nom, Instructions, et Ingrédients traduits EN → FR
    displayMeal(translatedMeal);
});
```

**Voir [INTEGRATION_THEMEALDB.md](INTEGRATION_THEMEALDB.md) pour le guide complet**.

---

## 🌍 Localisation

### Langues Supportées
- ✅ **Français** (par défaut)
- ✅ **Anglais** (English)

### Changement de Langue
1. Onglet **Profil** → **Paramètres**
2. **Langue** → Sélectionnez FR/EN
3. Interface + Recettes traduites instantanément

---

## 🐛 Dépannage

Si vous rencontrez des problèmes, consultez **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** pour :
- Erreurs de compilation courantes
- Problèmes d'exécution
- Issues de traduction
- Performance & mémoire

### Erreurs Courantes Résolues

```xml
<!-- ✅ Dimensions manquantes ajoutées -->
<dimen name="text_subtitle">14sp</dimen>
<dimen name="text_body_medium">14sp</dimen>

<!-- ✅ Couleurs manquantes ajoutées -->
<color name="text_primary">#1A1214</color>

<!-- ✅ Référence MaterialCardView mealCard corrigée -->
```

---

## 📖 Guide Utilisateur Rapide

### Démarrage
1. **Inscription/Connexion** : Email + mot de passe
2. **Accueil** : Explorez la Suggestion du Jour
3. **Recherche** : Cliquez "Découvrir" → Sélectionnez vos saveurs
4. **Résultats** : Naviguez avec swipe (← →)
5. **Détails** : Cliquez sur la recette pour ingrédients & instructions

### Fonctionnalités Clés
- ❤️ **Cœur** : Ajouter/retirer des favoris
- 🔄 **Swipe** : Naviguer entre les résultats
- 🌍 **Paramètres** : Changer la langue
- 📱 **Responsif** : Fonctionne sur tout appareil

---

## 🤝 Contribution

Les contributions sont bienvenues !

```bash
# Fork → Clone → Branch → Commit → Push → PR
git checkout -b feature/nom-feature
git commit -m "feat: description"
git push origin feature/nom-feature
```

**Conventions** :
- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `refactor:` Refactorisation
- `test:` Tests

---

## 📄 License

MIT License - Libre d'utilisation, modification, distribution.

[Voir LICENSE pour les détails complets](LICENSE)

---

## 📞 Support

- 🐛 **Issues** : Signalez les bugs sur GitHub
- 💬 **Discussions** : Questions et idées
- 📧 **Email** : contact@sipandsavour.com
- 🌐 **Web** : https://www.sipandsavour.com

---

## 📈 Roadmap

- **v1.1** : Sync favoris cloud, historique étendu
- **v1.2** : Mode hors ligne, IA avancée
- **v2.0** : Web app React, API GraphQL

---

## ✅ Checklist Installation

- [ ] Android Studio installé
- [ ] JDK 17+ configuré
- [ ] Git cloné
- [ ] Gradle synchronisé
- [ ] `local.properties` configuré
- [ ] Émulateur/Appareil connecté
- [ ] App lancée avec succès

---

## 🙏 Remerciements

- **TheMealDB** : API recettes gratuite
- **Google ML Kit** : Traduction sur-appareil
- **Material Design** : Composants UI
- **Communauté Android** : Support continu

---

**Version** : 1.0.0  
**Date** : Mars 2026  
**Mainteneur** : Équipe Sip & Savour

🍷 **Santé !** 🍷

