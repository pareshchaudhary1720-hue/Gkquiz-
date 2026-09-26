package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Repository(private val db: AppDatabase) {

    val currentAffairs: Flow<List<CurrentAffair>> = db.currentAffairDao().getAllCurrentAffairs()
    val subjects: Flow<List<Subject>> = db.subjectDao().getAllSubjects()
    val quizHistory: Flow<List<QuizHistory>> = db.quizHistoryDao().getQuizHistory()
    val quizzes: Flow<List<Quiz>> = db.quizDao().getAllQuizzes()

    fun getChaptersBySubject(subjectId: Int): Flow<List<Chapter>> = 
        db.chapterDao().getChaptersBySubject(subjectId)

    fun getBookmarkedChapters(): Flow<List<Chapter>> =
        db.chapterDao().getBookmarkedChapters()

    suspend fun getQuestionsForQuiz(quizId: Int): List<Question> =
        db.questionDao().getQuestionsForQuiz(quizId)

    suspend fun updateChapter(chapter: Chapter) {
        db.chapterDao().updateChapter(chapter)
    }

    suspend fun updateCurrentAffair(affair: CurrentAffair) {
        db.currentAffairDao().updateCurrentAffair(affair)
    }

    suspend fun insertQuizHistory(history: QuizHistory) {
        db.quizHistoryDao().insertQuizHistory(history)
    }

    suspend fun clearHistory() {
        db.quizHistoryDao().clearHistory()
    }

    // Dynamic insertions via Mock Admin Panel
    suspend fun addCurrentAffair(affair: CurrentAffair) {
        db.currentAffairDao().insertCurrentAffair(affair)
    }

    suspend fun addChapter(chapter: Chapter) {
        db.chapterDao().insertChapter(chapter)
    }

    suspend fun deleteCurrentAffair(id: Int) {
        db.currentAffairDao().deleteCurrentAffair(id)
    }

    suspend fun prepopulateIfEmpty() {
        // Prepopulate Subjects
        val currentSubjects = subjects.first()
        if (currentSubjects.isEmpty()) {
            val subs = listOf(
                Subject(1, "Indian Polity", "gavel", "Learn about the Constitution of India, fundamental rights, and governing structures."),
                Subject(2, "General Science", "science", "Explore the laws of Physics, Chemistry, and fundamental biological systems."),
                Subject(3, "Indian History", "history", "Discover ancient, medieval, and modern Indian historical movements."),
                Subject(4, "Geography & Environment", "public", "Understand atmospheric dynamics, earth features, and biodiversity conservation.")
            )
            for (sub in subs) {
                db.subjectDao().insertSubject(sub)
            }

            // Prepopulate Chapters for Indian Polity
            val polityChapters = listOf(
                Chapter(
                    subjectId = 1,
                    title = "Preamble & Salient Features",
                    durationText = "12 mins read",
                    notesText = "The Preamble to the Constitution of India is a brief introductory statement that sets out guidelines, which guide the people of the nation, and to present the principles of the Constitution.\n\n" +
                            "**Key Elements:**\n" +
                            "1. **Source of Authority:** The Preamble states 'We, the People of India...', which shows that the authority of the Constitution flows from the citizens.\n" +
                            "2. **Nature of Indian State:** India is declared as a Sovereign, Socialist, Secular, Democratic, Republican state.\n" +
                            "3. **Objectives:** To secure Justice, Liberty, Equality for all citizens, and to promote Fraternity assuring the dignity of the individual.\n\n" +
                            "**Amendments:**\n" +
                            "The Preamble has been amended only once, in 1976, by the 42nd Constitutional Amendment Act, which added three new terms: **Socialist**, **Secular**, and **Integrity**.\n\n" +
                            "**Significance & Court Cases:**\n" +
                            "• *Berubari Union Case (1960):* Supreme Court held that Preamble is NOT a part of the Constitution.\n" +
                            "• *Kesavananda Bharati Case (1973):* Supreme Court rejected the earlier opinion and held that Preamble IS a part of the Constitution. It can be amended under Article 368 but its 'Basic Structure' cannot be altered."
                ),
                Chapter(
                    subjectId = 1,
                    title = "Fundamental Rights (Part III)",
                    durationText = "20 mins read",
                    notesText = "Fundamental Rights are enshrined in Part III of the Constitution (Articles 12 to 35). They are described as the *Magna Carta* of India.\n\n" +
                            "**Six Fundamental Rights:**\n" +
                            "1. **Right to Equality (Articles 14–18):** Equal protection before law, prohibition of discrimination based on religion, race, sex, or birth.\n" +
                            "2. **Right to Freedom (Articles 19–22):** Freedom of speech, assembly, movement, trade, life & liberty, and protection against arrest.\n" +
                            "3. **Right against Exploitation (Articles 23–24):** Abolition of human trafficking and forced labor (begar), prohibition of child labor.\n" +
                            "4. **Right to Freedom of Religion (Articles 25–28):** Freedom of conscience, practice, and propagation of religion.\n" +
                            "5. **Cultural & Educational Rights (Articles 29–30):** Right of minorities to conserve language and establish educational institutes.\n" +
                            "6. **Right to Constitutional Remedies (Article 32):** Right to approach Supreme Court for enforcement of rights via writs (Habeas Corpus, Mandamus, Quo Warranto, Certiorari, Prohibition).\n\n" +
                            "**Key Characteristics:**\n" +
                            "• Fundamental Rights are **justiciable**, meaning citizens can directly approach courts for violation.\n" +
                            "• They can be suspended during a National Emergency (except Articles 20 and 21)."
                ),
                Chapter(
                    subjectId = 1,
                    title = "Directive Principles & Fundamental Duties",
                    durationText = "15 mins read",
                    notesText = "Part IV of the Constitution (Articles 36–51) details the Directive Principles of State Policy (DPSP), borrowed from the Irish Constitution.\n\n" +
                            "**Directive Principles (DPSP):**\n" +
                            "• They are non-justiciable guidelines for the State to establish economic and social democracy.\n" +
                            "• Divided into three broad categories: Socialistic, Gandhian, and Liberal-Intellectual.\n" +
                            "• Example: Article 40 (Village Panchayats), Article 44 (Uniform Civil Code), Article 45 (Early childhood education).\n\n" +
                            "**Fundamental Duties (Part IV-A):**\n" +
                            "• Included by the **42nd Amendment Act (1976)** on the recommendation of the Swaran Singh Committee.\n" +
                            "• Article 51A originally enumerated 10 duties. The 11th duty was added by the 86th Amendment Act (2002) regarding education for children aged 6-14.\n" +
                            "• Like DPSPs, Fundamental Duties are non-justiciable."
                )
            )
            for (ch in polityChapters) {
                db.chapterDao().insertChapter(ch)
            }

            // Prepopulate Chapters for General Science
            val scienceChapters = listOf(
                Chapter(
                    subjectId = 2,
                    title = "Human Circulatory System",
                    durationText = "10 mins read",
                    notesText = "The human circulatory system is a double circulation system containing the heart, blood vessels, and blood.\n\n" +
                            "**Structure of Heart:**\n" +
                            "• It is a 4-chambered muscular organ with two superior Atria (receiving chambers) and two inferior Ventricles (pumping chambers).\n" +
                            "• The left side of the heart handles oxygenated blood, while the right side handles deoxygenated blood, keeping them fully separated.\n\n" +
                            "**Circulation Paths:**\n" +
                            "1. **Pulmonary Circulation:** Right Ventricle pumps deoxygenated blood to the lungs via the pulmonary artery. In lungs, oxygen is absorbed and carbon dioxide is removed. Oxygen-rich blood returns to the Left Atrium via pulmonary veins.\n" +
                            "2. **Systemic Circulation:** Left Ventricle pumps oxygenated blood through the Aorta to all body tissues. Nutrients and oxygen are delivered, and metabolic waste is collected. Deoxygenated blood returns to the Right Atrium via the Vena Cava."
                ),
                Chapter(
                    subjectId = 2,
                    title = "Newton's Laws of Motion & Gravity",
                    durationText = "12 mins read",
                    notesText = "Sir Isaac Newton formulated the three fundamental laws of motion that govern classic physics, along with the Universal Law of Gravitation.\n\n" +
                            "**Three Laws of Motion:**\n" +
                            "1. **First Law (Inertia):** An object remains at rest or in uniform linear motion unless acted upon by an external net force.\n" +
                            "2. **Second Law (Force):** Force equals the rate of change of momentum. For a constant mass, Force = mass × acceleration (F = ma).\n" +
                            "3. **Third Law (Action-Reaction):** For every action, there is an equal and opposite reaction.\n\n" +
                            "**Universal Law of Gravitation:**\n" +
                            "Every particle in the universe attracts every other particle with a force directly proportional to the product of their masses and inversely proportional to the square of the distance between their centers: **F = G × (m1 × m2) / r²**, where G is the Gravitational Constant."
                )
            )
            for (ch in scienceChapters) {
                db.chapterDao().insertChapter(ch)
            }

            // Prepopulate Chapters for Indian History
            val historyChapters = listOf(
                Chapter(
                    subjectId = 3,
                    title = "Harappan Civilization",
                    durationText = "15 mins read",
                    notesText = "Also known as the Indus Valley Civilization, it represents one of the oldest urban civilizations of the ancient world (c. 2500 – 1900 BCE).\n\n" +
                            "**Key Characteristics:**\n" +
                            "• **Grid-pattern Planning:** Cities like Mohenjo-daro and Harappa featured rectangular grid streets, advanced drainage systems, and fortified citadels.\n" +
                            "• **Material Culture:** Famous for baked clay bricks, terracotta figurines, copper & bronze tools, and standard weight systems based on multiples of 16.\n" +
                            "• **Seals & Script:** Rectangular steatite seals with animal engravings (like the Unicorn or Pashupati) and a logo-syllabic pictographic script which remains undeciphered."
                ),
                Chapter(
                    subjectId = 3,
                    title = "The Maurya Empire & Ashoka",
                    durationText = "14 mins read",
                    notesText = "Founded by Chandragupta Maurya in 322 BCE after overthrowing the Nanda dynasty, with the strategic advice of Chanakya (Kautilya).\n\n" +
                            "**Emperor Ashoka and Dhamma:**\n" +
                            "• **Kalinga War (261 BCE):** The mass casualties and bloodshed of the Kalinga conquest filled Emperor Ashoka with remorse. He renounced military conquest (Bherighosha) and adopted conquest by piety/righteousness (Dhammaghosha).\n" +
                            "• **Edicts of Ashoka:** Inscribed on pillars, cave walls, and rock faces in Prakrit, Greek, and Aramaic. They propagated moral code, tolerance, and humane administration."
                )
            )
            for (ch in historyChapters) {
                db.chapterDao().insertChapter(ch)
            }

            // Prepopulate Geography & Environment
            val geographyChapters = listOf(
                Chapter(
                    subjectId = 4,
                    title = "Structure of the Atmosphere",
                    durationText = "10 mins read",
                    notesText = "The earth's atmosphere consists of five distinct, concentric layers based on temperature variations:\n\n" +
                            "1. **Troposphere (0-12 km):** Densest layer containing 75% of atmospheric mass. All weather phenomena (clouds, rain, storms) occur here.\n" +
                            "2. **Stratosphere (12-50 km):** Contains the **Ozone Layer** which absorbs harmful ultraviolet solar rays. It is clean and dry, making it ideal for flying commercial jet airplanes.\n" +
                            "3. **Mesosphere (50-80 km):** Coldest layer where temperature drops to -90°C. Meteors burn up in this layer upon entering from space.\n" +
                            "4. **Thermosphere (80-700 km):** Temperature increases rapidly with altitude. Contains the **Ionosphere** which reflects radio waves back to Earth for communications.\n" +
                            "5. **Exosphere (700-10,000 km):** The outermost layer merging into deep space, containing sparse helium and hydrogen gases."
                )
            )
            for (ch in geographyChapters) {
                db.chapterDao().insertChapter(ch)
            }

            // Prepopulate Quizzes
            val quizList = listOf(
                Quiz(101, "Indian Constitution & Polity MCQ", "Indian Polity", 3, 5),
                Quiz(102, "Human Biology & Health MCQ", "General Science", 2, 5)
            )
            for (quiz in quizList) {
                db.quizDao().insertQuiz(quiz)
            }

            // Prepopulate Quiz Questions
            val questions = listOf(
                // Quiz 101 questions
                Question(
                    id = 1,
                    quizId = 101,
                    questionText = "Which part of the Indian Constitution deals with Fundamental Rights?",
                    optionA = "Part I",
                    optionB = "Part II",
                    optionC = "Part III",
                    optionD = "Part IV",
                    correctOption = "C",
                    explanation = "Part III of the Indian Constitution (Articles 12 to 35) defines the Fundamental Rights, which are legally enforceable in courts of law."
                ),
                Question(
                    id = 2,
                    quizId = 101,
                    questionText = "Under which article of the Constitution is the Right to Equality guaranteed?",
                    optionA = "Article 21",
                    optionB = "Articles 14 to 18",
                    optionC = "Article 32",
                    optionD = "Article 19",
                    correctOption = "B",
                    explanation = "Articles 14 to 18 of the Constitution of India guarantee the Right to Equality as a fundamental right of all citizens."
                ),
                Question(
                    id = 3,
                    quizId = 101,
                    questionText = "The idea of Preamble in the Indian Constitution is borrowed from the Constitution of which country?",
                    optionA = "United Kingdom",
                    optionB = "Ireland",
                    optionC = "USA",
                    optionD = "Canada",
                    correctOption = "C",
                    explanation = "The Preamble is inspired by the Constitution of the USA, which was the first modern constitution to begin with a Preamble."
                ),
                Question(
                    id = 4,
                    quizId = 101,
                    questionText = "Which constitutional amendment added the word 'Socialist' to the Preamble?",
                    optionA = "42nd Amendment Act (1976)",
                    optionB = "44th Amendment Act (1978)",
                    optionC = "24th Amendment Act (1971)",
                    optionD = "86th Amendment Act (2002)",
                    correctOption = "A",
                    explanation = "The 42nd Constitutional Amendment Act of 1976 added three words: Socialist, Secular, and Integrity to the Preamble."
                ),
                Question(
                    id = 5,
                    quizId = 101,
                    questionText = "Who was the Chairman of the Drafting Committee of the Indian Constitution?",
                    optionA = "Mahatma Gandhi",
                    optionB = "Dr. B.R. Ambedkar",
                    optionC = "Dr. Rajendra Prasad",
                    optionD = "Jawaharlal Nehru",
                    correctOption = "B",
                    explanation = "Dr. B.R. Ambedkar was the Chairman of the Drafting Committee and is universally recognized as the Father of the Indian Constitution."
                ),

                // Quiz 102 questions
                Question(
                    id = 6,
                    quizId = 102,
                    questionText = "Which chamber of the human heart pumps oxygenated blood to the entire body?",
                    optionA = "Left Atrium",
                    optionB = "Right Atrium",
                    optionC = "Left Ventricle",
                    optionD = "Right Ventricle",
                    correctOption = "C",
                    explanation = "The Left Ventricle pumps highly oxygenated blood through the aorta to the systemic circulation of the body."
                ),
                Question(
                    id = 7,
                    quizId = 102,
                    questionText = "What is the normal blood pH range for a healthy human?",
                    optionA = "6.50 - 7.00",
                    optionB = "7.35 - 7.45",
                    optionC = "8.00 - 8.50",
                    optionD = "5.50 - 6.00",
                    correctOption = "B",
                    explanation = "Human blood is slightly basic, with a very tight normal physiological range of 7.35 to 7.45."
                ),
                Question(
                    id = 8,
                    quizId = 102,
                    questionText = "Which organelle is universally referred to as the powerhouse of the cell?",
                    optionA = "Nucleus",
                    optionB = "Ribosome",
                    optionC = "Mitochondria",
                    optionD = "Lysosome",
                    correctOption = "C",
                    explanation = "Mitochondria generate adenosine triphosphate (ATP), the chemical energy currency of the cell."
                ),
                Question(
                    id = 9,
                    quizId = 102,
                    questionText = "Which vitamin deficiency leads to the disease known as Scurvy?",
                    optionA = "Vitamin A",
                    optionB = "Vitamin B12",
                    optionC = "Vitamin C",
                    optionD = "Vitamin D",
                    correctOption = "C",
                    explanation = "Vitamin C (ascorbic acid) is essential for collagen synthesis; its deficiency leads to bleeding gums and Scurvy."
                ),
                Question(
                    id = 10,
                    quizId = 102,
                    questionText = "How many chromosomes are present in a normal human somatic cell?",
                    optionA = "23 chromosomes",
                    optionB = "46 chromosomes",
                    optionC = "48 chromosomes",
                    optionD = "52 chromosomes",
                    correctOption = "B",
                    explanation = "Normal human somatic cells contain 46 chromosomes (23 pairs)."
                )
            )
            db.questionDao().insertQuestions(questions)

            // Prepopulate Current Affairs
            val currentAffairsList = listOf(
                CurrentAffair(
                    title = "India Successfully Launches Gaganyaan Crew Escape Test",
                    category = "Science & Tech",
                    date = "Sep 26, 2026",
                    content = "The Indian Space Research Organisation (ISRO) successfully accomplished a crucial test vehicle flight for its Gaganyaan human spaceflight program. The mission demonstrated safe escape system function and parachute deployment under supersonic conditions, boosting crew safety standards."
                ),
                CurrentAffair(
                    title = "Global Hydrogen Energy Summit Concludes in New Delhi",
                    category = "Environment",
                    date = "Sep 25, 2026",
                    content = "Delegates from over 80 nations concluded the high-level Global Hydrogen Energy Summit. Highlights include the ratification of a unified $100 billion hydrogen infrastructure investment fund and specific timelines to transition to zero-carbon energy."
                ),
                CurrentAffair(
                    title = "RBI Keeps Benchmark Repo Rate Unchanged at 6.5%",
                    category = "Economy",
                    date = "Sep 24, 2026",
                    content = "The Monetary Policy Committee of the Reserve Bank of India decided to retain the policy repo rate under the liquidity adjustment facility at 6.50 percent, maintaining its stance on 'withdrawal of accommodation' to manage inflation."
                )
            )
            for (affair in currentAffairsList) {
                db.currentAffairDao().insertCurrentAffair(affair)
            }
        }
    }
}
