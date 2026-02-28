package com.codinglance.mydinningmap.feature


// ─────────────────────────────────────────────────────────────────
//  SAMPLE DATA REPOSITORY
//  Replace with your real data source (Room DB / API / etc.)
// ─────────────────────────────────────────────────────────────────

object JourneyRepository {

    val sampleJourneys: List<Journey> = listOf(

        // ── Journey 1: Mumbai City Tour ───────────────────────────
        Journey(
            id = 1,
            name = "Gurgaon City",
            description = "A full day exploring the best of Mumbai",
            date = "Dec 15, 2024",
            coverEmoji = "🌆",
            totalDistanceKm = 34.6f,
            stops = listOf(
                JourneyStop(
                    id = 1,
                    title = "Haldiram",
                    address = "Sahara Mall Shopping Complex M.G.Raod,Gurugram",
                    notes = "Started early at 7am. Light traffic.",
                    latitude = 28.4794338,
                    longitude = 77.0864217,
                    timestamp = 1734232200000L,
                    stopType = StopType.START,
                    distanceFromPrev = 0f,
                    durationAtStop = 5
                ),
                JourneyStop(
                    id = 2,
                    title = "YouMee",
                    address = "Unit No GR-02, Ground Floor, Worldmark Gurgaon, Village Maidawas, Sector 65, Gurugram, Haryana – 122001",
                    notes = "Iconic colonial architecture. Very crowded in the morning but worth it. Watched boats leave for Elephanta.",
                    latitude = 28.3980361,
                    longitude = 77.0726711,
                    timestamp = 1734236400000L,
                    stopType = StopType.PHOTO,
                    distanceFromPrev = 12.3f,
                    durationAtStop = 45
                ),
                JourneyStop(
                    id = 3,
                    title = "Barista Coffee",
                    address = "Shop No.D1 And D13-Fround Floor, Dlf Phase Qutub Plaza, Gurugram",
                    notes = "Famous breakfast spot. Had keema pav and chai. Great vibes, old-school feel.",
                    latitude = 28.4714694,
                    longitude = 77.1020038,
                    timestamp = 1734239400000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 0.6f,
                    durationAtStop = 60
                ),
                JourneyStop(
                    id = 4,
                    title = "P.F. Chang",
                    address = "Unit No. 6, Ground Floor, DLF Phase 2, Sector 24, DLF Cyber City, Gurgaon",
                    notes = "Famous breakfast spot. Had keema pav and chai. Great vibes, old-school feel.",
                    latitude = 28.4950716,
                    longitude = 77.0884522,
                    timestamp = 1734239400000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 0.6f,
                    durationAtStop = 60
                ),
                JourneyStop(
                    id = 5,
                    title = "Haldiram MGF Metropolitan Mall",
                    address = "MGF Metropolitan Mall, MG Road, Gurgaon",
                    notes = "Picked up fresh fruits and spices. Chaotic but fun experience. Got mangoes and cashews.",
                    latitude = 28.4809652,
                    longitude = 77.0803792,
                    timestamp = 1734246000000L,
                    stopType = StopType.VISIT,
                    distanceFromPrev = 0.7f,
                    durationAtStop = 40
                ),
                JourneyStop(
                    id = 6,
                    title = "Punjab Grill Tappa",
                    address = "Shop 19, Ground floor, DLF Cybercity, Gurgaon-122002",
                    notes = "Had their famous chicken sandwich and beer. Love the Mario Miranda murals on the walls.",
                    latitude = 28.4959442,
                    longitude = 77.0888396,
                    timestamp = 1734252000000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 3.2f,
                    durationAtStop = 75
                ),
                JourneyStop(
                    id = 7, title = "OMI by Aldott",
                    address = "The Aldott, 81, Moulsari Ave, DLF Phase 3, Sector 24, Gurugram, Haryana",
                    notes = "Walked the Queen's Necklace at golden hour. Beautiful sunset view. Sat at the sea wall for an hour.",
                    latitude = 28.492682,
                    longitude = 77.1076935,
                    timestamp = 1734260400000L,
                    stopType = StopType.VISIT,
                    distanceFromPrev = 4.1f,
                    durationAtStop = 60
                ),
                JourneyStop(
                    id = 8,
                    title = "My Secret Place",
                    address = "SCO 14, Main Palam Vihar Road, Palam Vihar, Sector 23A, Gurugram",
                    notes = "Quick coffee stop before heading home.",
                    latitude = 28.5049246,
                    longitude = 77.0527208,
                    timestamp = 1734267600000L,
                    stopType = StopType.REST,
                    distanceFromPrev = 9.8f,
                    durationAtStop = 25
                ),
                JourneyStop(
                    id = 9,
                    title = "The Drunken Botanist",
                    address = "Unit 1B & 1C, Upper Ground Floor-C, Building 10C, Cyber Hub, DLF Cyber City, Gurgaon",
                    notes = "Back home after an amazing day. Feet ache but memories made!",
                    latitude = 28.4938892,
                    longitude = 77.0883356,
                    timestamp = 1734270000000L,
                    stopType = StopType.END,
                    distanceFromPrev = 1.8f,
                    durationAtStop = 0
                ),
                JourneyStop(
                    id = 10,
                    title = "Daryaganj",
                    address = "Ambience Mall DLF Phase 3, Haryana",
                    notes = "Back home after an amazing day. Feet ache but memories made!",
                    latitude = 28.5055767,
                    longitude = 77.0962019,
                    timestamp = 1734270000000L,
                    stopType = StopType.END,
                    distanceFromPrev = 1.8f,
                    durationAtStop = 0
                )
            )
        ),

        // ── Journey 2: Delhi Heritage Walk ────────────────────────
        Journey(
            id = 2,
            name = "Delhi Heritage Walk",
            description = "Old Delhi to New Delhi monuments trail",
            date = "Jan 4, 2025",
            coverEmoji = "🏛️",
            totalDistanceKm = 22.4f,
            stops = listOf(
                JourneyStop(
                    id = 10, title = "Hotel – Connaught Place",
                    address = "Connaught Place, New Delhi 110001",
                    notes = "Checked out of hotel after breakfast.",
                    latitude = 28.6315, longitude = 77.2167,
                    timestamp = 1735971600000L,
                    stopType = StopType.START,
                    distanceFromPrev = 0f, durationAtStop = 10
                ),
                JourneyStop(
                    id = 11, title = "India Gate",
                    address = "Rajpath, India Gate, New Delhi 110001",
                    notes = "War memorial dedicated to soldiers. Grand structure. Lots of families picnicking around.",
                    latitude = 28.6129, longitude = 77.2295,
                    timestamp = 1735974000000L,
                    stopType = StopType.PHOTO,
                    distanceFromPrev = 3.2f, durationAtStop = 40
                ),
                JourneyStop(
                    id = 12, title = "Humayun's Tomb",
                    address = "Mathura Road, Nizamuddin East, New Delhi 110013",
                    notes = "Precursor to the Taj Mahal. Stunning Mughal architecture. Gardens were immaculately maintained.",
                    latitude = 28.5933, longitude = 77.2507,
                    timestamp = 1735979400000L,
                    stopType = StopType.VISIT,
                    distanceFromPrev = 5.1f, durationAtStop = 75
                ),
                JourneyStop(
                    id = 13, title = "Paranthe Wali Gali",
                    address = "Chandni Chowk, Old Delhi, Delhi 110006",
                    notes = "Best stuffed parathas in India! Had aloo and paneer paratha with chole. Absolutely amazing.",
                    latitude = 28.6562, longitude = 77.2310,
                    timestamp = 1735988400000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 8.7f, durationAtStop = 50
                ),
                JourneyStop(
                    id = 14, title = "Red Fort",
                    address = "Netaji Subhash Marg, Lal Qila, Old Delhi 110006",
                    notes = "Massive Mughal fortress. The Lahori Gate is breathtaking. Sound & light show info collected for evening.",
                    latitude = 28.6562, longitude = 77.2410,
                    timestamp = 1735992000000L,
                    stopType = StopType.VISIT,
                    distanceFromPrev = 1.1f, durationAtStop = 90
                ),
                JourneyStop(
                    id = 15, title = "Jama Masjid",
                    address = "Jama Masjid Road, Chandni Chowk, Old Delhi 110006",
                    notes = "One of the largest mosques in India. Climbed the minaret for panoramic old Delhi views.",
                    latitude = 28.6507, longitude = 77.2334,
                    timestamp = 1735998600000L,
                    stopType = StopType.PHOTO,
                    distanceFromPrev = 1.0f, durationAtStop = 45
                ),
                JourneyStop(
                    id = 16, title = "Back to Hotel",
                    address = "Connaught Place, New Delhi 110001",
                    notes = "Exhausted but fulfilled. Covered 22km today!",
                    latitude = 28.6315, longitude = 77.2167,
                    timestamp = 1736006400000L,
                    stopType = StopType.END,
                    distanceFromPrev = 3.3f, durationAtStop = 0
                )
            )
        ),

        // ── Journey 3: Goa Beach Hopping ──────────────────────────
        Journey(
            id = 3,
            name = "Goa Beach Hopping",
            description = "North Goa beaches in one perfect day",
            date = "Feb 10, 2025",
            coverEmoji = "🏖️",
            totalDistanceKm = 48.2f,
            stops = listOf(
                JourneyStop(
                    id = 17, title = "Resort – Candolim",
                    address = "Candolim Beach Road, Candolim, Goa 403515",
                    notes = "Early start to beat the beach crowds.",
                    latitude = 15.5189, longitude = 73.7606,
                    timestamp = 1739165400000L,
                    stopType = StopType.START,
                    distanceFromPrev = 0f, durationAtStop = 10
                ),
                JourneyStop(
                    id = 18, title = "Aguada Fort",
                    address = "Sinquerim, Bardez, North Goa 403519",
                    notes = "17th century Portuguese fort. Lighthouse still operational. Stunning views of Arabian Sea.",
                    latitude = 15.5015, longitude = 73.7732,
                    timestamp = 1739167800000L,
                    stopType = StopType.PHOTO,
                    distanceFromPrev = 4.2f, durationAtStop = 45
                ),
                JourneyStop(
                    id = 19, title = "Baga Beach",
                    address = "Baga, North Goa 403516",
                    notes = "Lively beach. Had fresh coconut water. Watched parasailing. Great energy here.",
                    latitude = 15.5556, longitude = 73.7519,
                    timestamp = 1739172600000L,
                    stopType = StopType.REST,
                    distanceFromPrev = 7.8f, durationAtStop = 60
                ),
                JourneyStop(
                    id = 20, title = "Brittos – Baga",
                    address = "Baga Beach Road, Baga, Goa 403516",
                    notes = "Famous beach shack. Had kingfish recheado and prawn curry rice. Cold Kingfisher beer too!",
                    latitude = 15.5572, longitude = 73.7511,
                    timestamp = 1739176800000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 0.3f, durationAtStop = 90
                ),
                JourneyStop(
                    id = 21, title = "Anjuna Flea Market",
                    address = "Anjuna Beach, North Goa 403509",
                    notes = "Iconic flea market. Bought a hand-painted bag and silver jewellery. Lots of hippie vibes.",
                    latitude = 15.5736, longitude = 73.7403,
                    timestamp = 1739184000000L,
                    stopType = StopType.VISIT,
                    distanceFromPrev = 5.1f, durationAtStop = 75
                ),
                JourneyStop(
                    id = 22, title = "Vagator Beach Sunset",
                    address = "Vagator, North Goa 403509",
                    notes = "Best sunset of the trip. Sat on the red cliffs as the sun dipped into the sea. Magical.",
                    latitude = 15.5996, longitude = 73.7440,
                    timestamp = 1739192400000L,
                    stopType = StopType.PHOTO,
                    distanceFromPrev = 4.6f, durationAtStop = 50
                ),
                JourneyStop(
                    id = 23, title = "Thalassa – Vagator",
                    address = "Small Vagator Beach, Ozran, Goa 403509",
                    notes = "Greek taverna with sea view. Had Mediterranean mezze platter and fresh lime soda.",
                    latitude = 15.5981, longitude = 73.7446,
                    timestamp = 1739196000000L,
                    stopType = StopType.FOOD,
                    distanceFromPrev = 0.4f, durationAtStop = 75
                ),
                JourneyStop(
                    id = 24, title = "Resort – Candolim",
                    address = "Candolim Beach Road, Candolim, Goa 403515",
                    notes = "Long drive back. Stars were incredible on the way.",
                    latitude = 15.5189, longitude = 73.7606,
                    timestamp = 1739203800000L,
                    stopType = StopType.END,
                    distanceFromPrev = 25.8f, durationAtStop = 0
                )
            )
        )
    )
}