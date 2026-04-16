# Rapport final — Projet Structures de Données II
## Problème du Fenêtrage (Windowing)

**Université de Mons — Année académique 2025-2026**  
**Auteur : Abdellah Boumaiza**

---

## Table des matières

1. [Introduction](#1-introduction)
2. [Définitions et notations](#2-définitions-et-notations)
3. [Structures de données](#3-structures-de-données)
   - 3.1 [Priority Search Tree (PST)](#31-priority-search-tree-pst)
   - 3.2 [Interval Tree](#32-interval-tree)
4. [Algorithme de fenêtrage](#4-algorithme-de-fenêtrage)
5. [Design Pattern Strategy](#5-design-pattern-strategy)
6. [Architecture du projet](#6-architecture-du-projet)
7. [Interface graphique](#7-interface-graphique)
8. [Compilation et exécution](#8-compilation-et-exécution)
9. [Tests](#9-tests)
10. [Conclusion](#10-conclusion)

---

## 1. Introduction

Le **problème du fenêtrage** (*windowing problem*) consiste à retrouver efficacement, parmi un ensemble de $n$ segments axis-parallèles dans le plan, tous ceux qui intersectent un rectangle de requête donné.

Une approche naïve comparerait la fenêtre à chacun des $n$ segments, ce qui donne une complexité $O(n)$ par requête. L'objectif de ce projet est d'atteindre une complexité **$O(\log n + k)$** par requête, où $k$ est le nombre de segments retournés, en s'appuyant sur deux structures de données avancées : l'**arbre de recherche de priorité** (PST) et l'**arbre d'intervalles** (*Interval Tree*), telles que décrites dans *Computational Geometry* de de Berg et al.

### Illustration du problème

```
  y
  |
8 +      |              |
  |      | (vert. ✓)    |
6 +      |    ╔═════════╪══════╗
  |      |    ║         |      ║
4 + ─────┼────╫─────────┼──────╫──── (horiz. ✓)
  |      |    ║         |      ║
2 +      |    ╚═════════╪══════╝
  |      |              |
0 +──────┴──────────────┴──────────> x
       xMin           xMax

  ✓ = segment intersectant la fenêtre W (rectangle en double trait)
```

---

## 2. Définitions et notations

### 2.1 Segments axis-parallèles

Un **segment horizontal** est un segment de la forme $\{(x, y_0) \mid x_1 \leq x \leq x_2\}$, dont les deux extrémités partagent la même ordonnée $y_0$.

Un **segment vertical** est un segment de la forme $\{(x_0, y) \mid y_1 \leq y \leq y_2\}$, dont les deux extrémités partagent la même abscisse $x_0$.

### 2.2 Fenêtre de requête

Une **fenêtre de requête** est un rectangle axis-parallèle fermé :

$$W = [x_{\min}, x_{\max}] \times [y_{\min}, y_{\max}]$$

Les bornes peuvent être infinies ($-\infty$ ou $+\infty$) pour représenter des fenêtres semi-illimitées.

Un segment $s$ **intersecte** $W$ si et seulement si l'un des trois cas suivants se produit :

```
  Cas 1 : extrémité       Cas 2 : croise          Cas 3 : traverse
          à l'intérieur           un bord                  entièrement
  ┌──────────┐            ┌──────────┐             ┌──────────┐
  │  •───────│──          │ ─────────│──────────   │          │
  │          │            │          │           ──│──────────│──
  └──────────┘            └──────────┘             └──────────┘
   → trouvé par PST        → trouvé par            → trouvé par
                             Interval Tree           Interval Tree
```

### 2.3 Nombres composites (Section 5.5)

Pour traiter correctement les coordonnées dupliquées sans hypothèse de généricité, on remplace chaque point $p = (p_x, p_y)$ par un point composite :

$$\hat{p} = \big((p_x \mid p_y),\ (p_y \mid p_x)\big)$$

où $(a \mid b)$ désigne le **nombre composite** de composante principale $a$ et secondaire $b$, ordonné lexicographiquement :

$$(a \mid b) < (a' \mid b') \iff a < a' \text{ ou } (a = a' \text{ et } b < b')$$

```
  Point réel            Point composite
  p = (px, py)    →    p̂ = ( (px|py),  (py|px) )
                              ───┬───    ───┬───
                           clé-x heap  clé-y BST
```

Une requête $[x : x'] \times [y : y']$ doit être transformée en :

$$[(x \mid -\infty) : (x' \mid +\infty)] \times [(y \mid -\infty) : (y' \mid +\infty)]$$

afin d'inclure correctement les points situés exactement sur les bords de la fenêtre.

### 2.4 Notations

| Notation | Signification |
|----------|---------------|
| $n$ | Nombre de segments |
| $k$ | Nombre de segments retournés par une requête |
| $W = [x_{\min}, x_{\max}] \times [y_{\min}, y_{\max}]$ | Fenêtre de requête |
| $(a \mid b)$ | Nombre composite de composantes $a$ et $b$ |
| $\hat{p}$ | Version composite du point $p$ |
| $cx(p)$ | $(p_x \mid p_y)$ — clé composite en $x$ |
| $cy(p)$ | $(p_y \mid p_x)$ — clé composite en $y$ |

---

## 3. Structures de données

### 3.1 Priority Search Tree (PST)

#### 3.1.1 Définition

Le **Priority Search Tree** (de Berg et al., Chapitre 10) est une structure hybride combinant :
- Un **tas** (*heap*) sur la clé composite $cx$ : la racine contient le point de $cx$ minimale dans le sous-arbre.
- Un **arbre binaire de recherche (BST)** sur la clé composite $cy$ : chaque nœud interne stocke la médiane $cy$ qui partage les points restants entre les sous-arbres gauche et droit.

```
  Structure d'un nœud PST :
  ┌──────────────────────────────┐
  │ entry    : point (min cx)    │
  │ medianCy : médiane cy        │
  │ left     : sous-arbre gauche │  (cy ≤ medianCy)
  │ right    : sous-arbre droit  │  (cy > medianCy)
  └──────────────────────────────┘
```

#### 3.1.2 Exemple de PST

Considérons 5 points : (1,2), (2,5), (3,4), (4,3), (5,1).

```
             (1,2)          ← min cx = (1|2), retiré comme racine
           medianCy=(3|4)
           /             \
       (4,3)             (2,5)       ← cx minimal de chaque moitié
     medianCy=(1|5)    medianCy=(4|3)
       /                    \
    (5,1)                  (3,4)
```

- **Propriété de tas** : chaque nœud a la clé $cx$ minimale de son sous-arbre.
- **Propriété BST** : le sous-arbre gauche contient tous les points avec $cy \leq medianCy$, le droit avec $cy > medianCy$.

#### 3.1.3 Construction

**Algorithme BUILDPST($P$) :**

```
Entrée : ensemble de points P
Sortie : racine du PST

BUILDPST(P) :
  si P est vide alors retourner null

  trier P par cx croissant
  minEntry ← retirer le point de cx minimale de P

  node ← nouveau nœud(minEntry)
  si P est vide alors retourner node

  trier P par cy croissant
  node.medianCy ← cy( P[|P|/2] )

  left  ← { p ∈ P | cy(p) ≤ node.medianCy }
  right ← { p ∈ P | cy(p) > node.medianCy }

  node.left  ← BUILDPST(left)
  node.right ← BUILDPST(right)
  retourner node
```

**Complexité de construction :** $O(n \log n)$ en temps, $O(n)$ en espace.

#### 3.1.4 Requête ouverte à gauche

Le PST répond à des requêtes de la forme $(-\infty, x_{\max}] \times [y_{\min}, y_{\max}]$.

```
  Principe de l'élagage (pruning) :
  ┌─────────────────────────────────────────────────────┐
  │ Si cx(v.entry) > keyBound                           │
  │   → tout le sous-arbre a cx > keyBound (heap)       │
  │   → on peut couper                                  │
  │                                                     │
  │ Si cyLo > v.medianCy                                │
  │   → aucun point du sous-arbre gauche n'est dans [cyLo, cyHi] │
  │   → on ne descend pas à gauche                      │
  │                                                     │
  │ Si cyHi ≤ v.medianCy                                │
  │   → aucun point du sous-arbre droit n'est dans [cyLo, cyHi]  │
  │   → on ne descend pas à droite                      │
  └─────────────────────────────────────────────────────┘
```

**Algorithme QUERYPST($v$, $keyBound$, $cyLo$, $cyHi$) :**

```
Entrée : nœud v, borne sup composite keyBound = (xMax|+∞),
         cyLo = (yMin|−∞), cyHi = (yMax|+∞)

QUERYPST(v, keyBound, cyLo, cyHi) :
  si v = null alors retourner

  si cx(v.entry) > keyBound alors
    retourner  // élagage par propriété de tas

  si cyLo ≤ cy(v.entry) ≤ cyHi alors
    signaler(v.entry)

  si v.left ≠ null et cyLo ≤ v.medianCy alors
    QUERYPST(v.left, keyBound, cyLo, cyHi)

  si v.right ≠ null et cyHi > v.medianCy alors
    QUERYPST(v.right, keyBound, cyLo, cyHi)
```

**PST négatif :** pour les requêtes $x \geq x_{\min}$, on construit un second PST avec la clé $(-p_x \mid -p_y)$. La requête $x \geq x_{\min}$ devient $-x \leq -x_{\min}$, soit une requête ouverte à gauche avec borne $(-x_{\min} \mid +\infty)$.

**Complexité de requête :** $O(\log n + k)$ dans le pire des cas.

---

### 3.2 Interval Tree

#### 3.2.1 Définition

L'**arbre d'intervalles** permet de retrouver en $O(\log n + k)$ tous les segments dont l'intervalle primaire **contient** un point de requête donné.

Deux instances sont construites :
- **Arbre horizontal** : clé primaire = intervalle en $x$, coordonnée transversale = $y$.
- **Arbre vertical** : clé primaire = intervalle en $y$, coordonnée transversale = $x$.

#### 3.2.2 Structure

```
  Structure d'un nœud Interval Tree :
  ┌─────────────────────────────────────────────────────┐
  │ midPoint    : médiane des extrémités                │
  │ leftSorted  : segments contenant midPoint,          │
  │               triés par extrémité gauche ↑          │
  │ rightSorted : segments contenant midPoint,          │
  │               triés par extrémité droite ↓          │
  │ leftChild   : segments entièrement à gauche         │
  │ rightChild  : segments entièrement à droite         │
  └─────────────────────────────────────────────────────┘
```

#### 3.2.3 Exemple d'Interval Tree

Considérons 4 segments horizontaux : $[1,8]$, $[2,6]$, $[3,10]$, $[7,9]$ au même $y$.

```
  Extrémités : 1,2,3,6,7,8,9,10 → midPoint = 7

            midPoint = 7
            leftSorted  = [1,8], [2,6], [3,10]   (triés par min ↑)
            rightSorted = [3,10], [1,8]           (triés par max ↓)
           /                     \
      midPoint=3              midPoint=8
      leftSorted=[2,6]        leftSorted=[7,9]
      rightSorted=[2,6]       rightSorted=[7,9]
       /
  midPoint=1.5
  leftSorted=[1,8] ← non, [1,8] contient 3 donc il est au nœud parent
```

#### 3.2.4 Construction

**Algorithme BUILDINTERVALTREE($S$) :**

```
Entrée : ensemble de segments S
Sortie : racine de l'arbre d'intervalles

BUILDINTERVALTREE(S) :
  si S est vide alors retourner null

  endpoints ← { min(s), max(s) | s ∈ S }
  midPoint  ← médiane de endpoints

  mid   ← { s ∈ S | min(s) ≤ midPoint ≤ max(s) }
  left  ← { s ∈ S | max(s) < midPoint }
  right ← { s ∈ S | min(s) > midPoint }

  node.leftSorted  ← trier mid par min(s) croissant
  node.rightSorted ← trier mid par max(s) décroissant
  node.leftChild   ← BUILDINTERVALTREE(left)
  node.rightChild  ← BUILDINTERVALTREE(right)
  retourner node
```

**Complexité de construction :** $O(n \log n)$ en temps, $O(n)$ en espace.

#### 3.2.5 Requête

**Algorithme QUERYCROSSING($v$, $point$, $crossMin$, $crossMax$) :**

```
Entrée : nœud v, point à tester,
         bornes transversales [crossMin, crossMax]

QUERYCROSSING(v, point, crossMin, crossMax) :
  si v = null ou v.leftSorted = null alors retourner

  si point < v.midPoint alors
    pour chaque s dans v.leftSorted faire
      si min(s) ≤ point alors
        si crossMin ≤ cross(s) ≤ crossMax alors signaler(s)
      sinon break  // tri garantit l'arrêt précoce ← O(k) au lieu de O(n)
    QUERYCROSSING(v.leftChild, point, crossMin, crossMax)

  sinon
    pour chaque s dans v.rightSorted faire
      si max(s) ≥ point alors
        si crossMin ≤ cross(s) ≤ crossMax alors signaler(s)
      sinon break
    QUERYCROSSING(v.rightChild, point, crossMin, crossMax)
```

**Complexité de requête :** $O(\log n + k)$ dans le pire des cas.

---

## 4. Algorithme de fenêtrage

### 4.1 Principe

Un segment $s$ intersecte $W$ si et seulement si au moins l'une des conditions suivantes est vraie :

1. **Une extrémité de $s$ est dans $W$** → détecté par les requêtes PST.
2. **$s$ traverse $W$ sans extrémité intérieure** → détecté par les requêtes Interval Tree.

```
  Vue d'ensemble de l'algorithme :

  Segments chargés
       │
       ▼
  ┌─────────────────────────────────────────────┐
  │              Index (PstIndex)               │
  │  ┌──────────┐  ┌──────────┐                │
  │  │PST avant │  │PST négatif│               │
  │  │(cx croiss)│  │(-cx crois)│               │
  │  └──────────┘  └──────────┘                │
  │  ┌──────────┐  ┌──────────┐                │
  │  │IT horiz. │  │IT vert.  │                │
  │  └──────────┘  └──────────┘                │
  └─────────────────────────────────────────────┘
       │
  Fenêtre W = [xMin, xMax] × [yMin, yMax]
       │
       ▼
  Étape 1 : PST → extrémités dans W       O(log n + k₁)
  Étape 2 : IT horiz → croise x = xMin    O(log n + k₂)
  Étape 3 : IT vert  → croise y = yMin    O(log n + k₃)
       │
       ▼
  Résultats dédupliqués (k = k₁+k₂+k₃)
```

Pour l'étape 2, il suffit de tester **un seul bord** par type de segment :
- Un segment horizontal traversant $W$ sans extrémité intérieure coupe nécessairement le **bord gauche** ($x = x_{\min}$).
- Un segment vertical traversant $W$ sans extrémité intérieure coupe nécessairement le **bord inférieur** ($y = y_{\min}$).

### 4.2 Pseudo-code principal

```
Algorithme WINDOWING(index, segments, W) :
  xMin, xMax, yMin, yMax ← bornes de W

  // --- Étape 1 : requêtes PST ---
  endpointHits ← liste vide

  si xMin = -∞ et xMax = +∞ alors
    endpointHits ← PST_avant.queryYStrip(yMin, yMax)

  sinon si xMax = +∞ alors
    endpointHits ← PST_négatif.queryOpenLeft(-xMin, yMin, yMax)
    filtrer : retirer les points avec x > xMax

  sinon
    endpointHits ← PST_avant.queryOpenLeft(xMax, yMin, yMax)
    si xMin ≠ -∞ alors filtrer : retirer les points avec x < xMin

  résultats ← ensemble { segments[e.index] | e ∈ endpointHits }

  // --- Étape 2 : requêtes Interval Tree ---
  si xMin ≠ -∞ alors
    IT_horiz.queryCrossing(xMin, yMin, yMax, résultats)

  si yMin ≠ -∞ alors
    IT_vert.queryCrossing(yMin, xMin, xMax, résultats)

  retourner résultats
```

### 4.3 Complexité

| Étape | Complexité |
|-------|-----------|
| Construction de l'index | $O(n \log n)$ |
| Requête PST | $O(\log n + k_1)$ |
| Requête IT horizontal | $O(\log n + k_2)$ |
| Requête IT vertical | $O(\log n + k_3)$ |
| **Total par requête** | $\mathbf{O(\log n + k)}$ |

---

## 5. Design Pattern Strategy

Le **patron de conception Strategy** est utilisé pour sélectionner automatiquement l'algorithme approprié selon les bornes de la fenêtre.

```
             <<interface>>
          WindowingStrategy
      + execute(index, segments, W)
                  ▲
    ┌─────────────┼──────────────┬──────────────┬──────────────┐
    │             │              │              │              │
Bounded       Left           Right          Bottom          Top
Window        Bounded        Bounded        Bounded         Bounded
Strategy      Strategy       Strategy       Strategy        Strategy

[xMin,xMax]  (-∞,xMax]     [xMin,+∞)    [xMin,xMax]    [xMin,xMax]
×[yMin,yMax] ×[yMin,yMax]  ×[yMin,yMax] ×(-∞,yMax]     ×[yMin,+∞)
```

La sélection est effectuée par le `WindowingController` selon la règle suivante :

```
si xMin = -∞  → LeftBoundedWindowStrategy
si xMax = +∞  → RightBoundedWindowStrategy
si yMin = -∞  → BottomBoundedWindowStrategy
si yMax = +∞  → TopBoundedWindowStrategy
sinon         → BoundedWindowStrategy
```

---

## 6. Architecture du projet

### 6.1 Structure des packages

```
org.windowing.windowingproject
│
├── model/                      ← objets du domaine
│   ├── CompositeNumber         ← nombre composite (a|b), ordre lexico.
│   ├── Point2D                 ← point immutable avec cx, cy précalculés
│   ├── Segment                 ← segment axis-parallèle + intersects(Window)
│   ├── Window                  ← rectangle de requête (bornes ±∞ admises)
│   └── PstEntry                ← extrémité stockée dans le PST
│
├── pst/                        ← structures de données algorithmiques
│   ├── PrioritySearchTree      ← PST complet (build + query)
│   ├── PSTNode                 ← nœud du PST (entry, medianCy, left, right)
│   ├── IntervalTree            ← arbre d'intervalles (build + query)
│   ├── PstIndex                ← conteneur des 4 structures (2 PST + 2 IT)
│   └── PstWindowing            ← algorithme de fenêtrage principal
│
├── strategy/                   ← patron Strategy
│   ├── WindowingStrategy       ← interface
│   ├── BoundedWindowStrategy
│   ├── LeftBoundedWindowStrategy
│   ├── RightBoundedWindowStrategy
│   ├── BottomBoundedWindowStrategy
│   ├── TopBoundedWindowStrategy
│   └── WindowingContext        ← exécute la stratégie choisie
│
├── ui/                         ← interface graphique JavaFX
│   ├── DrawingPane             ← canvas + transform monde↔écran + drag
│   └── WindowingController     ← logique métier (load, query, sélection)
│
└── util/
    └── FileLoader              ← lecture du fichier de données
```

### 6.2 Flux de données

```
  Fichier .txt
      │  FileLoader.loadSegments()
      ▼
  List<Segment>
      │  WindowingController.load()
      ▼
  PstIndex (2 PST + 2 IT)
      │  WindowingController.query(Window)
      ▼
  WindowingStrategy.execute()
      │  PstWindowing.findIntersectingSegments()
      ▼
  List<Segment> résultats
      │  DrawingPane.highlightSegments()
      ▼
  Affichage (segments verts)
```

---

## 7. Interface graphique

L'interface est développée en **JavaFX** et se compose de deux zones principales.

```
┌──────────────────┬──────────────────────────────────────────┐
│   CONFIGURATION  │                                          │
│                  │                                          │
│ [Charger Fichier]│     Zone de dessin (DrawingPane)         │
│                  │                                          │
│ xMin : [    ]    │   ┌────────────────────────────────┐    │
│ xMax : [    ]    │   │  segments gris (non trouvés)   │    │
│ yMin : [    ]    │   │  ╔══════════════╗               │    │
│ yMax : [    ]    │   │  ║ segments     ║               │    │
│                  │   │  ║ verts ✓      ║               │    │
│[Lancer Recherche]│   │  ╚══════════════╝               │    │
│                  │   │   (fenêtre bleue pointillée)    │    │
│  STATISTIQUES    │   └────────────────────────────────┘    │
│  Total    : 1000 │                                          │
│  Trouvés  : 434  │   ← drag souris pour dessiner W         │
│  Temps    : 2 ms │                                          │
└──────────────────┴──────────────────────────────────────────┘
```

### 7.1 Chargement d'un fichier

Le bouton **« Charger Fichier »** ouvre un sélecteur de fichier. Le format attendu est :

```
xMin xMax yMin yMax        ← fenêtre de visualisation (1ère ligne)
x1 y1 x2 y2               ← segment 1
x1 y1 x2 y2               ← segment 2
...
```

Après chargement, les segments apparaissent en gris sur le canvas, et le compteur *Total* est mis à jour.

### 7.2 Saisie de la fenêtre de requête

Les quatre champs **xMin**, **xMax**, **yMin**, **yMax** acceptent :
- Un nombre décimal (ex : `-500.0`).
- Les chaînes `-inf` ou `+inf` pour une borne infinie.
- Un champ vide : interprété comme $-\infty$ pour les bornes inférieures, $+\infty$ pour les bornes supérieures.

### 7.3 Sélection par glisser-déposer

L'utilisateur peut **dessiner directement** la fenêtre de requête sur le canvas par un glisser-déposer (*drag & drop*). Un rectangle bleu semi-transparent s'affiche en temps réel. À la fin du drag, les coordonnées sont automatiquement converties en coordonnées monde et la recherche est relancée.

```
  Transformation coordonnées écran → monde :

  worldX = mouseX / largeurCanvas × (xMax_monde - xMin_monde) + xMin_monde
  worldY = mouseY / hauteurCanvas × (yMax_monde - yMin_monde) + yMin_monde
```

### 7.4 Résultats

- Les **segments verts** sont ceux qui intersectent la fenêtre.
- Les **segments gris** sont ceux qui n'intersectent pas.
- Le **rectangle bleu pointillé** représente la fenêtre de requête, toujours affiché au premier plan.
- Le panneau *Statistiques* affiche le nombre de segments trouvés et le temps d'exécution en millisecondes.

---

## 8. Compilation et exécution

### Prérequis

- **Java 17** ou supérieur
- **Maven** (ou utiliser le wrapper `./mvnw` fourni dans le projet)

### Compilation

```bash
./mvnw compile
```

### Exécution

```bash
./mvnw javafx:run
```

### Exécution des tests

```bash
./mvnw test
```

### Format du fichier de données

```
-1000.0 1000.0 -1000.0 1000.0
652.0 -804.0 652.0 798.0
-49.0 211.0 -724.0 211.0
...
```

La première ligne définit la fenêtre de visualisation `[xMin xMax yMin yMax]`. Les lignes suivantes définissent les segments au format `x1 y1 x2 y2`.

---

## 9. Tests

Une suite de **60 tests unitaires** est fournie, organisée en cinq classes :

| Classe de test | Nb. tests | Éléments testés |
|----------------|-----------|-----------------|
| `CompositeNumberTest` | 11 | Ordre lexico., `lowerBound`, `upperBound`, `leq`, `geq`, cas aux bornes |
| `SegmentTest` | 14 | Intersection horiz./vert. avec diverses fenêtres, bornes infinies, bords |
| `PrioritySearchTreeTest` | 13 | `queryOpenLeft`, `queryYStrip`, PST négatif, coordonnées dupliquées, vide |
| `IntervalTreeTest` | 12 | Croisement horiz./vert., arrêt précoce, cas aux limites, arbre vide |
| `PstWindowingTest` | 10 | Algorithme complet : fenêtre bornée, semi-illimitée, déduplication |

```
  Résultat de la suite de tests :

  ┌──────────────────────────────────────────┐
  │  Tests run: 60, Failures: 0, Errors: 0  │
  │  BUILD SUCCESS                           │
  └──────────────────────────────────────────┘
```

---

## 10. Conclusion

Ce projet met en œuvre une solution efficace au problème du fenêtrage pour des segments axis-parallèles, atteignant une complexité **$O(\log n + k)$** par requête grâce à la combinaison du PST et de l'arbre d'intervalles. L'utilisation des nombres composites (Section 5.5 de de Berg et al.) garantit la correction de l'algorithme même en présence de coordonnées dupliquées. Le patron Strategy offre une architecture extensible pour traiter toutes les configurations de fenêtre. L'interface JavaFX permet une visualisation interactive avec sélection de la fenêtre par glisser-déposer, et une suite de 60 tests unitaires valide l'ensemble des composants.
