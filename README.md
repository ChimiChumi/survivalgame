Scala 2023 beadandó feladat

# A feladat

Egy leegyszerűsített sandbox játék (mint a Minecraft vagy a Terraria) szerverének implemen- tálása, enemy AI nélkül. A doksi hátsó (hosszú) részében találunk egy vázlatos példát.

**Világgenerálás, Enemy AI, GUI és kliens fejlesztése nem része a feladatnak!** 
A játék szerveroldalon a következőket kell tudja támogatni:

- Fontos: a feladatban minden osztály **immutable** legyen, azaz ne változzon élettartama során egyik mezőjének az értéke sem! Ugyanúgy, ahogy a kurzus legnagyobb részében látjuk, módosító műveletek helyett olyan metódusokat kell fejleszteni, melyek visszaadják eredményként az updatelt objektumot. Kizárólag **val**okat használjunk **var**okat semmiképp, a mutable packagekből se használjunk osztályokat.
- legyen egy (2D vagy 3D, mindegy) világtérkép, valamekkora méretű, „blokkokból” (azaz legyen cellákra osztva a térkép)
- egy blokk a térképen lehessen üres, vagy kitöltve pontosan egyféle „placable” objektummal (elég, ha az összes placable objektum pontosan egy blokkos, de persze implementálhatunk nagyobbakat is)
- a világban legyenek entityk: számítógép vezérelte mobok és playerek, playerekből több is tudjon belépni és kilépni a játékba, menet közben is, mobok pedig spawnolni (keletkezni valahol) tudjanak (ennek a logikáját nem kell megírjuk; a feladatunk az, hogy tudjunk kezelni egy olyan requestet, mint „spawnoljon egy zombi ide” vagy „tegyük be ezt az IDjű játékost, ezekkel az adatokkal és felszereléssel, ide” és ennek megfelelően updatelni a világot)
- minden entitynek legyen minimálisan egy maxHP, aktuális HP, sebesség, támadás, védekezés statja és egy aktuális pozíciója a térképen, ami mindig egy üres blokk
- a moboknak ennyi infó elég is, a játékosoknak ezen kívül legyen inventoryja, amibe ite- meket pakolhat, fix darab slotba, az egyes itemek valamilyen korlátig per slot legyenek stackelhetőek; legyenek továbbá fegyverek, páncélok és equipmentek is a játékban, külön- féle tulajdonságokkal (elég, ha a fegyvereknek támadás, a páncéloknak védelem értékük van, de persze fűszerezhetjük pl. a fegyverek melee / ranged / magic osztályozással, a páncélokat ezek elleni külön-külön védelemmel, páncélfajtákból többet is – sisak, mell- vért, csizma – felvehetünk stb); a játékos egyszerre maximum egy fegyvert és maximum egy páncélt (ha aláosztályozzuk a páncélokat fajtákra, akkor mindből max egyet-egyet) vehessen fel, egyéb equipmentekhez pedig szintén legyenek slotjai, melyekbe max egyet- egyet tehet a különféle equipmentekből; legyen továbbá egy „kurzoron mi van” slotja is, amibe szintén egyféle itemből kerülhet maximum egy stack (pl ezt használhatjuk ahhoz, hogy itemeket pakoljunk az inventory és egy láda közt)
- a játékosok statjait az equippelt fegyverek, páncélok és egyéb equipmentek módosítsák
- legyenek valamiféle effektek a játékban (pl. attack/defense növelés/csökkentés kon- stanssal, vagy százalékkal, max HP növelése, „mérgezés effekt” ami tickenként valamen- nyivel csökkenti a HP-t stb), melyeket az entitásokra rátehetünk valamekkora (tickekben mért) időtartamra (playerekre is, mobokra is), amik a statjaikat addig módosítják, amíg az idő le nem telik; ne csak az entitykre, de az equipmentekre is lehessen effekteket tenni, melyek pedig akkor hassanak az őket hordó játékosra (vagy mobra, ha úgy döntünk, hogy a moboknak is legyenek fegyver / armor / equipment slotjai, ez nem kötelező elem), ha az equipped slotokba rakjuk őket
- a weapon / armor / equipment / placable itemeken kívül legyenek consumable itemek is a játékban, melyeket a játékos el tud fogyasztani, ezek (az itemtől függően) adjanak a játékosra effekt(ek)et valamennyi ticken keresztül
- a játékos az inventoryjába „bányászással” bele tudja helyezni az inventoryjába a térkép egyes blokkjait is (ha beleférnek); „floating item”ek, melyek nem placablék, de bányászás után „lebegve a helyükön maradnak” és felszedhetővé válnak, implementálása nem kötelező, de persze lehetséges; hogy a játékos egy blokkot ki tud-e bányászni, az alsó hangon vegye figyelembe a játékos és a célblokk távolságát (különféle erejű bányászós csákányok implementálása nem kötelező eleme a feladatnak, de persze lehetséges, a lenti példa implementációban a játékos mindenféle blokkot ki tud bányászni egy tick alatt)
- a játékos a kezéből a placable blokkokat le is tudja rakni az üres blokkokra a térképen (ekkor a kezéből tűnjön el egy az ott tartott placable itemek közül, a térkép vonatkozó cellája pedig üresről váltson erre a fajta itemre), itt vegyük figyelembe a játékos és a célblokk távolságát
- legyenek ládák is a placable itemek közt, ezek is pontosan olyan (esetleg más méretű) inventoryként működjenek, mint a játékosok inventoryjai, azaz fix számú slottal bírjanak, és egy-egy slotba egyféle itemből tehessünk bele valamennyit, max annyit, amennyit az adott itemből stackelni lehet; a játékosok tudjanak az inventoryjuk és egy placed láda közt mozgatni itemeket
- legyen harcrendszer a játékban, ahol is minimum a támadó támadásától és a védő de- fensétől függjön, hogy a védő mennyit sérül (amit a mindkét résztvevőre ható effektek is befolyásoljanak), erre elég egy „ki üt éppen meg kit” requestet írni, a requestet kezelő függvénye a WorldStatenek (lásd lejjebb) pedig számolja ki a tényleges sebzést és ennek megfelelően aktualizált új worldstate-et adjon vissza. Cooldown implementálása (amíg használat után nem tudunk újra fegyvert használni) nem kötelező elem a feladatban, de persze lehetséges.
- legyen crafting rendszer is a játékban, ahol is egy-egy recept inputja valamennyi item (pl. két fa és három kő), outputja pedig egy item (pl. egy kőcsákány), ezeket a játékosok le tudják craftolni, ha van rá elég alapanyaguk az inventoryjukban, ekkor az alapanyagok tűnjenek el az inventoryjukból, a kezükben meg jelenjen meg az output item. Különféle crafting stationok (pl. azzal, hogy melyik receptet milyen crafting állomás közelében, ami szintén egy placable blokk, lehet elkészíteni) nem kötelező részei a feladatnak, de persze lehetséges.
- mobok halálakor ők tűnjenek el (XP rendszer, szintek megvalósítása nem kötelező része a feladatnak, de persze lehetséges), a játékosok pedig éledjenek újra valahol, valamilyen szabályok szerint (pl. elveszthetik akár az összes felszerelésüket, de meg is tarthatják őket, az effektjeiket is, a HP-jukat beállíthatjuk a maxra vagy a felére, stb)
- mindezt funkcionálisan úgy valósítsuk meg, hogy legyen egy WorldState osztályunk, melynek egy objektumában tároljuk a világról az összes infót (térképek, belépett játékosok, statjaikkal, inventoryjukkal, effektjeikkel stb, a világban létező mobok, crafting receptek), és beérkezett, még kezelendő Request objektumok egy kollekcióját, a World- Statenek pedig egy függvénye (a lentiekben vázlatosan kidolgozott példa implementá- cióban ez a processNextRequest metódus) a listában következő requestet kezelje le, és adja vissza az updatelt WorldState-et (melyben tehát az eredetihez képest módosul a térkép és/vagy az entitások, illetve a hátralévő még kezeletlen requestek listája)
- a játékban az idő múlását „tick”ekben mérjük, legyen egy „Tick” request, amit ha a world state update lekezel, akkor eggyel léptesse az eltelt tickek számát; ami mindenképp eleme kéne legyen a játéknak, az az, hogy az effektek közt legyenek olyanok, melyek valamennyi tick elteltéig aktívak az entityken, annak leteltével pedig kerüljenek le róluk
- a szerver feladata legyen annak ellenőrzése, hogy a soron következő request valid-e; ha nem az, akkor a requestet feldolgozó függvény magát a world statet ne változtassa meg (attól eltekintve, hogy a requestek vektorából a visszaadott world state példányban kerüljön ki az invalid request). Példa, amikor invalid a request: egy entity oda akar mozogni, ahova a maximális sebességével egy tick alatt nem jut el, vagy amikor úgy próbál kibányászni egy blokkot, hogy az nem fér már el az inventoryjában
- legyen lehetőség elmenteni és betölteni egy komplett worldstate-et (teljesen jó, ha az egész worldstate egyetlen file-ba kerül, akár pl. egy JSON objektumként – ekkor egy toJson: String és egy JSON-t váró konstruktor írása a feladat része, de lehet másmilyen mentési formátumot is választani)
- lehessen a világról statisztikákat, adatokat gyűjteni, azaz a WorldState-nek legyenek olyan metódusai, melyek pl. legyűjtik és visszaadják játékosonként az összes náluk lévő itemek darabszámait (ha teli ládát is el tudunk rakni az inventoryba, akkor azok tartalmát is), legyűjtik az összes olyan itemet, ami valamilyen recept inputjai, vagy épp outputjai, az egyes játékosoktól megadott távolságon belül lévő mobok számát típusonként megadva, stb.
- készítsünk teszteket is az összetettebb metódusainkhoz; alsó hangon elég, ha néhány konstans inputra, amire kézzel kiszámoljuk, hogy minek kéne lennie az eredménynek, összehasonlítjuk az outputot az elvárt outputtal.

## Az első mérföldkő tennivalói

**Az első mérföldkő** határideje 2023. április 9. 23:59:59. Eddig a feladat: deklarálni az im- plementációban használt traiteket , case classokat (a fejlécükben jelzett adattagjaikkal együtt) , és azok publikus metódusainak fejléceit (ideértve a konstruktorokat is, ha van- nak), de ezen a ponton minden metódus implementációja még lehet ??? is; dokumentáljuk le (scaladocban elég) az összes traitet, case classt és metódust, hogy melyiknek mi az elvárt viselkedése, mire jó, mit ad vissza az eredeti objektumhoz képest.

**A második mérföldkő** tennivalói

Megírni a teszteket és a ???ek helyére a tényleges implementációkat; szükség esetén újabb privát metódusokat persze hozzátehetünk. Ha ezek után még pluszban bővítjük ki a játékot olyan elemekkel, amik az első mérföldkőben még nem léteztek, az persze teljesen rendben van. Ha az első mérföldkő metódusai valamelyikének a fejlécét megváltoztatjuk, vagy nem azt csinálja, mint az eredeti doksiban írtuk, akkor arról írjunk, hogy miért mégse úgy lett megvalósítva, mint ahogy eredetileg elképzeltük.

### Egy példa rendszerterv

Egy lehetséges vázlatos megvalósítás pl. olyasmi lehet, mint ami a doksi maradék részében szerepel. Nem kötelező pont így megvalósítani, ez csak ihletet ad, hogy hogy is nézhet pl. ki egy ilyen task belülről.

## WorldState

Egy **WorldState** objektum fogja tárolni a világot: a (2D) térképen hol milyen blokkok vannak, hol vannak a világban a játékosok és a mobok, azoknak az attribútumait és a rajtuk lévő effekteket, a világban a ládák tartalmát, a világ szabályait stb.

A játék során ahogy az idő telik, persze a WorldState más és más lesz, de sose az eredeti WorldState-ünket kell módosítanunk, hanem új WorldState-eket létrehozni. A módosításokat Request-ekben kapjuk; a WorldState tároljon egy Request szekvenciát is, a még feldolgozásra váró requestekkel.

### A WorldState metódusai:

- **hasRequests: Boolean:** adja vissza, van-e még feldolgozatlan request.
- **processNextRequest: WorldState** ha nincs több feldolgozatlan request, vagy nincs benn player a játékban, akkor adjuk vissza az eredeti WorldState-et. Különben vegyük a soron következő requestet, vegyük ki a request listából és aktualizáljuk a state-et en- nek megfelelően. 

*Pl. ha a következő request egy (MoveEntity(id, position)), a világban tényleg jelen van id ID-jű entitás, a position pedig egy olyan pozíció, ahova az entitás az aktuális pozíciójából egy tick alatt el tud jutni, akkor a visszaadott új WorldState a state- hez képest annyiban más, hogy ez az entitás oda kerül benne, egyébként pedig ugyanaz, mint state; a request pedig kikerül a hátralévő requestek vektorából.*

- **processNextRequest: WorldState**: addig processzelje a requesteket, amíg azok el nem fogy- nak; adja vissza az ezután előálló state-et.

- **players: Vector[Player]** adja vissza a világba aktuálisan belépett játékosokat
- **apply(x: Int, y: Int): Option[Placable]** adja vissza a térkép adott koordinátáján lévő blokkot egy opcióban; ha a térképen ezen a pozíción nincs blokk (és így átjárható, „üres”), akkor legyen None, egyébként az ott levő Placable egy Someban.
- **apply(position: Position): Option[Placable]** mint a másik, de Positionra működjön; a Posi- tionbeli floatok lefelé kerekítésével kapjuk meg az egész koordinátákat.
- **width: Int, height: Int** adja vissza a térkép szélességét és magasságát. (Az első koordinátát 0-tól width− 1-ig indexelhetjük, a másodikat 0-tól height− 1-ig.)

## Item

Egy felszedhető item típust tároljon. Mindegyik Itemnek legyen egy neve (két különböző Itemnek nem lehet ugyanaz a neve). Továbbá, mindegyik Itemnek legyen egy maxStackSize Int értéke, aminek pozitívnak kell lennie, ez határozza meg, hogy egy-egy inventory slotba maximum mennyit rakhatunk az adott típusú itemből.

Ennek a típusnak az altípusai legyenek a következők:

- **Placable**: ezek a lerakható blokkok, ilyenekből áll a térkép is. A lerakott blokkokat a játékosok kibányászhatják, amikor is az inventoryjukba fognak kerülni.

- **Consumable**: a játékosok az inventoryjukban lévő Consumable itemeket megehetik, ettől az adott item eltűnik, a játékos pedig kap egy vagy több effektet, mindegyiket valamilyen időtartamig. A consumable-ek tárolják, hogy milyen effekteket és milyen időtartamra (ha nem instant az effekt) adnak.
- **Weapon**: a játékosoknak a dedikált weapon slotjukban lehet egyszerre maximum egy Weaponjuk (persze az inventoryjukban lehet annyi emellett, amennyi csak fér). A Weaponok maxStackSizea mindenképp legyen 1. Legyen nekik damage: Int sebzésük (po- zitív szám).
- **Armor**: a játékosoknak a dedikált armor slotjukban lehet egyszerre maximum egy Ar- morjuk. Az Armorok maxStackSizea mindenképp legyen 1. Legyen nekik defense: Int védelmük (pozitív szám).
- **Equipment**: a játékosoknak van fixszámú equipment slotjuk, mindegyikben maximum egy Equipment tárolható. Az Equipmentek maxStackSizea mindenképp legyen 1. Egy Equip- ment adhasson effekteket, ami akkor legyen aktív az őt viselő játékoson, ha az equipment slotjai valamelyikében tartja.

### ItemStack

Ez a típus tároljon egy Itemet és egy darabszámot; csak úgy tudjuk létrehozni, ha a darabszám nemnegatív, és legfeljebb annyi, mint az Itemnek a maxStackSizea.


#### Az ItemStack metódusai:

- **+(that: ItemStack): (ItemStack, Option[ItemStack])** 
megpróbálja egy stackbe rakni a két itemstacket. Ha a két stack itemjei különböznek, akkor adjuk vissza az első koordinátán a bal, a másodikon a jobb oldali eredeti stacket (persze optionben); ha egyformák, és az összes mennyiségük elfér egyben, akkor a visszaadott tuple első koordinátájába kerüljön az egyberakott stack, a másodikba pedig kerüljön None, ha nem fér el, akkor a bal oldaliba rakjunk amennyit lehet, a jobb oldaliba pedig a maradékot!

*Pl. ha a fának a maximális stackmérete 16, és egy 10 méretű fa stackhez adunk hozzá egy másik 10 méretű fa stacket, akkor az eredmény bal oldalán egy 16 méretű fa stack lesz, a jobb oldalán pedig egy 4 méretű fa stack egy optionben. Ha ugyanezt a két stacket adjuk össze, de a maximális stackméret 32, akkor kapjunk a bal oldali koordinátán egy 20 méretű fa stacket, a jobb oldalin pedig None-t. Ha fához adunk követ, akkor a bal oldaliban lesz az eredeti famennyiség, a jobb oldaliban az eredeti kőmennyiség egy optionben.*

## Chest

A Chest legyen a Placable altípusa. Legyen neki egy capacityje, ami megadja, hogy hány pakol- ható slot van benne, alapból minden slotja legyen üres. Legyen továbbá egy id: String mezője is (a világban nem lehet egyszerre kettő, azonos ID-jű entitás vagy chest).

- **isEmpty: Boolean**: adja vissza, hogy a láda üres-e.

- **capacity: Int** adja vissza a láda-beli slotok számát.
- **apply(i: Int): Option[ItemStack]** adja vissza a láda megadott pozícióján lévő ItemStacket egy opcióba csomagolva, ha ez a slot nem üres; ha 0-nál kisebb vagy legalább capacity a kapott index, vagy ha a megfelelő slot üres, kapjunk Nonet.
- **+(stack: ItemStack): (Chest, Option[ItemStack])** megpróbálja a chestbe berakni a kapott stacket, ha fér, az aktualizált chestet adja vissza a bal oldali koordinátán, az esetleges maradékot, ami már nem fért a ládába, a másodikon.

**Pakoláskor**

- ha már eleve van a ládában ilyen itemből nem-full stack, akkor elsősorban azt/azokat próbálja feltölteni a maximális stack méretig; ha (már ezután) nincs, akkor az első üres slotba tegye a maradékot; ha nincs üres slot se és még mindig van a berakni kívánt stackből, akkor a maradékot adja vissza a második koordinátán.

- **swap(index: Int, stack: ItemStack): (Chest, Option[ItemStack])** próbálja meg betenni az indexedik slotba a chestben az érkező stacket. Ha nincs ilyen index, akkor adja vissza az eredeti ládát és az eredeti stacket; ha van ilyen index, akkor a visszaadott érték láda komponensében az épp berakott stack legyen, az itemstack opció komponensében pedig az ezen a pozíción eredetileg lévő tartalom! (Ami lehet épp None is, ha ebben a slotban nem volt eredetileg a chestben semmi.)
- **contains(item: Item): Boolean** adja vissza, hogy van-e a ládában a megadott itemből.
- **count(item: Item): Int** adja vissza, hogy összesen mennyi van a ládában a megadott itemből (adja össze azoknak a stackeknek a méretét, amikben ez az item van).

## EntityStats

Egy élő/mozgó entitás statjai: attack: Int támadás, defense: Intvédelem, speed: Doublesebesség (blocks per tick) maxHP: Int maximális életerő, regeneration: Double regenerálódás (HP per tick).

Metódusok

- **applyEffect(effect: Effect): EntityStats** adja vissza az effekt alapján módosított statokat.

pl. ha az effekt egy IncreaseAttack(5) effekt, és az eredeti stat attackja 3, akkor a vissza- adott statban az attack 8 legyen)

- **applyEffect(effect: Effect\*): EntityStats** adja vissza a kapott effektek mindegyike (sorban applikálva őket) alapján módosított statokat.

## Entity

Egy élő/mozgó entitás. Mindegyiknek legyen String neve, String ID-je (az ID a világban unique lesz, minden entitásnak más-más ID-je lesz, a neve minden játékosnak más-más, a moboknak pedig a nevük a fajtájuk), baseStats statjai, lehessen rajta nulla, egy vagy több aktív Effect, mindegyik egy-egy Durationnel, legyen currentHP: Int életereje és position: Position pozíciója. Mindig figyeljünk arra, hogy az entitás életereje semmiképp nem lehet több, mint az effek-

tek applikálása után (ld lejjebb) kapott maximális életereje; ha több lenne, azt javítsuk ki a konstruktorban, hogy ne lépje túl.

Altípusai: **Mob** és **Player**.

Metódusok

Minden entitásnak legyenek a következő metódusai:

- **baseStats: EntityStats** : adja vissza az entitás alap statjait.

- **heal(hp: Int): Entity**: a visszaadott entitásnak ennyivel legyen több az életereje, de legfel- jebb a maximális HP-ig gyógyulhasson; ha hp negatív, itt ne történjen semmi és adjuk vissza az eredeti entitást.

*Note: mivel lehetséges, hogy az entitáson van olyan effekt, ami a maximális HP-ját változ- tatja meg, itt az effektekkel megváltoztatott statok szerinti (ld applyEffects) maxHP-ját vizsgáljuk. (Ezt a checket érdemes lehet konstruktorba tenni.)*

**Metódusok:**
- **takeDamage(hp: Int): Option[Entity]**: a visszaadott entitásnak ennyivel csökkenjen az életereje. Ha így még pozitív marad, adjuk vissza az új entitást (optionben), ha nullára vagy az alá csökken, akkor None-t.
- **addEffect(effect: Effect, duration: Duration): Entity** : az entitás effect listájára kerüljön fel ez az effect, a megadott időtartamig.
	- Ha ez az effekt nincs még rajta az entitáson, akkor pluszban kerüljön hozzá.
	- Ha már rajta van, akkor a meglévő és az új duration közül a nagyobb maradjon meg.

- **removeEffects(p: Effect⇒ Boolean): Entity**: az entitásról vegye le az összes olyan effektet, amire igaz a p predikátum
- **applyEffects: EntityStats**: adja vissza az entitás alap statjait, megváltoztatva őket az entitásra rakott effektek alapján.
- **moveTo(position: Position): Entity**: kerüljön át az entitás a megadott pozícióra! Itt nem kell vizsgálnia a speed értéket.
- **tick: Option[Entity]**: adja vissza, hogy egy tickkel később mivé válik ez az entitás. Adott esetben akár el is pusztulhat 
	- *pl. mérgezés/vérzés stb. effekt miatt negatív a regene- rálódása, ami egy tick múlva leviszi 0-ra vagy az alá a HPjá), ekkor az opcióban Nonet adjunk vissza, egyébként a megváltoztatott entitást (opcióba csomagolva).* 

	- *Note: egy tick alatt a következők történnek egy entitással: i) a rajta lévő effektek durationje egy tickkel csökken, ii) a hp-je az effektekkel megváltoztatott regenerálódásnak megfelelően változik. Figyeljünk arra, hogy ha épp lejár egy effekt, ami a maxHP-t növelte eddig, akkor a HP már a kövi tickben az új maxHP fölé nem mehet, és ezt a felső korlátot alkalmazzuk.*

A Mob típusú entitásoknak ezeken kívül nincs más jellemzőjük. (note: érdemes lehet pl. mob típusonként egy-egy case classt létrehozni, ami a konstruktorában pl. az ID-t, hpját, pozícióját és az effektjeit kapja meg, a base statjait meg egy-egy megfelelő, a classhoz tartozó konstansra inicializálja.)

## Player

Egy, a világba belépett játékos. Az Entity-n kívül még a további jellemzőkkel bírjon:

- Konstruktorban kapjon meg egy capacity: Int értéket, és egy ekkora item inventory legyen nála (az inventoryhoz használjuk itt is a Chest osztályt).
- Szintén konstruktorban kapjon meg egy equipmentSlots értéket, egy ekkora tárolója is legyen, amibe – a Chesthez hasonlóan – Equipmenteket lehet pakolni a pozíciókra.

*Note: itt gondolkodjunk el azon, hogy hogy tudnánk megoldani az equipment tárolót úgy, hogy ne kelljen újraimplementálnunk a Chest-beli metódusok közül semmit kétszer, de ebbe a tárolóba kizárólag Equipmenteket tudjunk pakolni.*

- A Playerekre vonatkozó applyEffects metódus, miután applikálja az összes, a játékoson mint entitáson lévő effektet, applikálja az összes, az equipment tárolóban lévő equip- mentekhez kapcsolt effektet is és ezt az eredményt adja vissza. Itt az azonos effektek hatásai összeadódhatnak, pl. ha effektből és két equipmentből is növekszik a játékos attackja, akkor mind a három növekmény hasson, és az így módosított statokat adjuk vissza.

- A removeEffects csak a játékosra tett effekteket szűrje ki, az equipmentekhez kapcsolt effektek maradjanak aktívak.
- Legyen egy-egy dedikált slotja (ami az inventoryn és az equipment slotokon kívül van) legfeljebb egy Weaponnak és legfeljebb egy Armornak.
- Base statjának az attack értéke legyen 1, ha a weapon slotja üres; egyébként a benne lévő fegyver damage értéke.
- Base statjának a defense értéke legyen 0, ha az armor slotja üres; egyébként a benne lévő armor defense értéke.
- Legyen egy onCursor: ItemStack tárolója is, ide kerül a játékos által „bekattintott”, a kurzoron lévő item stack (ha van, pl. amikor az inventoryjából egy ládába helyez át egy item stacket).
- Legyen egy respawnPosition: Position értéke is (ide kerül vissza halál esetén).
- Legyen egy reachingDistance: Doubleértéke is (ilyen messzire tud bányászni / ládát pakolni stb. a saját pozíciójától).

## Position

Egy x: Double és egy y: Double értéket (koordináta-párt) tartalmazó osztály.

## Duration

Egy effekt hatásának hátralévő időtartama. Altípusai:

- **TicksLeft(ticks: Int)** ennyi ticken keresztül tart még a hatás.
- **TillDeath**: az effekt aktív marad, amíg a játékos meg nem hal.
- **Permanent**: az effekt aktív marad, halál után is.

*Érdemes lehet ennek is adni egy tick metódust, ami egy Option[Duration]-t ad vissza, az egy tick múlva érvényben lévő durationt, ami épp lejáró TicksLeft esetén None lesz, egyébként eggyel csökkentett ticks értékű TicksLeft, a másik két duration pedig egy tickkel később is ugyanaz maradna.*

## Effect

Egy effekt egy entitás statjaira hat, implementáljunk ilyeneket. Néhány ötlet pl. lehet:

- **IncreaseDamage(value: Int)**, ami a damage-t növeli egy konstanssal
- **ScaleDefense(percentage: Double)**, ami a defenset szorozza fel egy konstanssal
- **Poison(value: Int)**, ami a regenerálódás értékét csökkenti egy konstanssal

*Note: érdemes lehet minden egyes effektnek egy-egy case classt felvenni.*

Metódusok

- **apply(stats: EntityStats): EntityStats** adja vissza, hogy az inputként érkezett statokat mivé alakítja át az adott effekt. 

*(note: jó volna, ha az effektek tudnák magukról, hogy ők hogyan hatnak, az entity és a stats pedig az effekteknek ezt a metódusát hívva mó- dosítgatná a statokat, semmit ne implementáljunk le kétszer.)*

## Recipe

Egy crafting receptet leíró osztály, pl. „három fából és egy üvegből lehessen nagyítót készíteni”. (Inputként egy vagy több itemstacket, outputként egy itemet tároljunk benne.)

## GameRules

A világ szabályai:

- tartalmazzák a világban lévő összes item típust
- tartalmazzák a világban alkalmazható összes receptet

Metódusok

- **getItems(p: Item => Boolean)** adja vissza az összes olyan itemet a világban, amire igaz a p predikátum

- **getPlacables** adja vissza az összes Placable itemet, hasonlóan a getWeapons stb. az öt item osztály mindegyikére
- **materials** adja vissza az összes olyan itemet, ami szerepel receptben nyersanyagként
- **craftables** adja vissza az összes olyan itemet, ami szerepel receptben outputként

## Request

A világban történő egy-egy eventet ábrázoló típus. Altípusai és hatásaik a WorldState.handle metódusában (ezek pl. mind lehetnek egy-egy case class ill. a tick esetén case object):

- **Tick**: ilyen request esetén eggyel teljen az idő. Ekkor az összes entity tickjét hívjuk és az eredménnyel updateljük az egyes entitásokat; ha egy entity egy tick után meghal, akkor ezután a requests lista végére tegyünk egy Die requestet is a megfelelő ID-vel.
- **Join(player: Player)**: ilyen request esetén player szeretne bejoinolni a világba. Ha egy WorldState handeli ezt a requestet, akkor amennyiben ilyen IDjű játékos nincs még bent, akkor engedje be és vegye fel a player listára, ha már van, akkor ne változtassa meg a state-et.
- **LeavePlayer(id: String)**: ilyen request esetén ha van ilyen ID-jű játékos bent, akkor vegyük ki a játékosok listájáról.
- **Die(id: String)**: ilyen request esetén az adott ID-jű entitást vegyük ki a state-ből (ha van). Amennyiben ez egy Mob volt, nincs más tennivalónk; ha Player, akkor pedig vegyük le az effektjeit, a HP-ját állítsuk be a maximum HP-ja felére (felfelé kerekítve), és helyezzük át a state-ben a respawn pozíciójára.
- **Mine(id: String, position: Position)**: ha van benn ezzel az ID-vel játékos, a térképen a megadott pozíción van valamilyen blokk, és a játékos inventoryjába elfér még egy ilyen blokk (azaz egy olyan item stack, amiben ilyen itemből van egy darab), és még a játékos pozíciójától annak a reaching distance-én belül is van a megcélzott pozíció (note: itt is a játékosra előbb applikáljuk az effekteket, mert miért is ne lehetne olyan effekt, ami változtatja a reaching distance-et), akkor helyezzük át a térképről a játékos inventoryjába ezt az item„stack”et; egyébként változatlanul adjuk vissza a WorldState-et.

- **StoreItem(playerID: String, chestID: String)**: ha van ilyen ID-jű player és ilyen ID-jű chest, a kettő egymástól legfeljebb a játékos reaching distance-ére van, és a játékos kurzor slotja nem üres, akkor próbáljuk meg betenni a játékos kezében lévő item stacket a ládába; a játékos kezében maradjon az a stacknyi item, ami már nem fér bele a ládába (tehát ha az egész belefér, akkor None marad a kezében, ha részben fér, akkor annyi stb.)
- **LootItem(playerID: String, chestID: String, index: Int)**: ha van ilyen láda és játékos, elég közel egymáshoz, és a játékos keze üres, akkor helyezzük át a játékos kezébe a ládában a megfelelő indexen lévő item stacket. 

*(Note: ezt a fenti két metódust tudjuk pl. használni a játékos inventoryja és a keze közti mozgatásokra is, hiszen az inventory maga is egy Chest; ha az equipment slotokat és a fegyver / armor slotokat is chesttel implementáljuk, akkor arra is, de itt oda kell figyelnünk, hogy ezekbe a slotokba nem mozgathatunk át tetszőleges itemeket, csak amiknek stimmel a típusuk is.)*

- **CraftRecipe(playerID: String, recipe: Recipe)**: ha a játékosnak az inventoryjában van ele- gendő alapanyag összesen a recept minden egyes hozzávalójához, és a keze üres, akkor csökkentsük az inventoryja tartalmát a recept hozzávalóival, a kezébe pedig tegyük a recepttel előállított itemet (egy egyelemű stackként).
- **Consume(playerID: String)**: ha a játékosnak a kezében egy Consumablevan, akkor ürítsük ki a kezét és adjuk rá az effekt(ek)et,amiket az adott consumable ad, a megfelelő durationnel.
- **MoveEntity(entityID: String, position: Position)**: ha az adott IDjű entitás létezik, és egy tick alatt elér az aktuális pozíciójáról a megadottra, és ezen a megadott pozíción üres blokk van, akkor helyezzük át ezt az entitást az új helyre.

- **HitEntity(attackerID: String, defenderID: String)**: ha a két adott IDjű entitás létezik, és az attacker reaching distance-én belül van a defender, akkor a defender HP-ját csökkentsük az attacker attack értékével, mínusz a defender defense értékével, de legalább eggyel. (Ha így a defender HP-ja 0-ra vagy az alá csökkenne, akkor egy megfelelő Die requestet is szúrjunk a request lista végére.)

*Note: mivel a requestek egy world state-re hatnak, aminek adott esetben privát adattagjait is látniuk kell, a requesteket érdemes lehet sima adatosztályként implementálni mondjuk egy sealed traitet extendelve, és a hatásukat a ‘WorldState‘ osztályba kódolni. Az effektek körét bővíthetjük ízlés szerint.*
