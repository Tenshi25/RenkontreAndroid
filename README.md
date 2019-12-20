# RenkontreAndroid
Projet CCM2 en Binôme

**Conseils :**

* Penser à accepter l'ensemble des permissions au démarrage de l'application pour éviter des problèmes.

**Contributeurs**	
* Kévin CARDON 
* Rémi URBANIEC

**Sujet du projet**	

Vous allez développer une « petite » application Android qui permettra à ses utilisateurs de disposer d’une liste d’amis et d’une liste « d’ennemis ». Toutes ces personnes devront apparaitre sur une carte en fonction de leur localisation actuelle et la carte devra évoluer en temps-réel en fonction des déplacements des uns et des autres. L'idée derrière cette application est qu'elle devrait permettre à deux utilisateurs de constater en consultant la Map qu’ils sont proches l'un de l'autre, pour, s’ils le décident, procéder à un rapprochement s’ils sont amis ou à un éloignement s’ils sont « ennemis ». Les informations de géolocalisation seront partagées en « temps-réel » via la technologie FireBase et sera visualisée via des Marqueurs apparaissant sur des Google Maps. Votre application devra être conçue de façon suffisamment modulaire afin de permettre un « changement aisé » de technologie(s), par exemple, pour passer de FireBase à une autre technologie ou, par exemple, pour passer de GooleMap à OpenStreepMap.


**Liste des options acceptés et refusés**	

| Options | Accepté ou Refusé | 
|:-------------:|:------:|
| Une option pourrait permettre à votre application de déclencher l’application d’envoi de SMS ou d’appel téléphonique rien qu’en cliquant sur un Marqueur de la Map pour peu que l’utilisateur associé à ce Marqueur fasse partie de vos contacts (téléphonique).| Accepté |
| Une option pourrait permettre de filtrer sur un critère de distance les utilisateurs apparaissant sur les Maps. En effet, est-il pertinent de savoir qu'un autre utilisateur est très éloigné de vous puisque vous ne pourrez pas le rejoindre (possibilité de paramétrer cette distance) ?| Accepté |
|Une option mise en place cette fois ci sous forme de service tournant en tâche de fond pourrait permettre à un utilisateur de recevoir une "notification de distance/proximité" soit pour l'informer qu'un de ses amis est très proche soit pour l'informer que quelqu'un qu'il ne veut pas rencontrer est également très proche| Accepté |
| Un paramétrage de la fréquence de notification de déplacement pourra être proposé et une optionnelle dégradation automatique de cette fréquence pourra être proposée en vue d'économiser l'utilisation de la batterie.| Refusé |
