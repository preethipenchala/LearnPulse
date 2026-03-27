package com.learnpulse.data.mock

import com.learnpulse.domain.model.*

/**
 * Central source of mock data for testing all LearnPulse features.
 * Uses free public image/video CDNs — no API key required.
 *
 * Images: picsum.photos (Lorem Picsum)
 * Avatars: i.pravatar.cc
 * Videos:  Google sample videos (commondatastorage.googleapis.com)
 */
object MockDataSource {

    // ─── Instructors ─────────────────────────────────────────────────────────

    val instructors = listOf(
        Instructor(
            id = "inst-1",
            name = "Dr. Sarah Chen",
            avatarUrl = "https://i.pravatar.cc/150?img=47",
            bio = "Senior ML Engineer at Google with 12 years of experience in AI/ML. Published researcher and passionate educator.",
            courseCount = 8,
            rating = 4.9
        ),
        Instructor(
            id = "inst-2",
            name = "Marcus Rivera",
            avatarUrl = "https://i.pravatar.cc/150?img=12",
            bio = "Full-stack developer and tech lead with 10 years building scalable web apps. Loves Kotlin and clean architecture.",
            courseCount = 6,
            rating = 4.8
        ),
        Instructor(
            id = "inst-3",
            name = "Priya Sharma",
            avatarUrl = "https://i.pravatar.cc/150?img=31",
            bio = "UX/UI designer with expertise in design systems, accessibility, and Figma. Works with Fortune 500 companies.",
            courseCount = 5,
            rating = 4.7
        ),
        Instructor(
            id = "inst-4",
            name = "James Okoro",
            avatarUrl = "https://i.pravatar.cc/150?img=68",
            bio = "Data scientist and statistician. Former quant at Goldman Sachs turned educator. Python and R expert.",
            courseCount = 7,
            rating = 4.8
        ),
        Instructor(
            id = "inst-5",
            name = "Emily Watson",
            avatarUrl = "https://i.pravatar.cc/150?img=25",
            bio = "MBA from Harvard Business School. Startup founder and business strategy consultant for early-stage companies.",
            courseCount = 4,
            rating = 4.6
        ),
        Instructor(
            id = "inst-6",
            name = "Kenji Nakamura",
            avatarUrl = "https://i.pravatar.cc/150?img=57",
            bio = "Mathematics professor and competitive programmer. Makes abstract math concepts accessible and fun.",
            courseCount = 5,
            rating = 4.9
        )
    )

    // ─── Sample Video URLs (Google public sample videos) ─────────────────────

    private val videoUrls = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"
    )

    private fun video(index: Int) = videoUrls[index % videoUrls.size]

    // ─── Lessons ─────────────────────────────────────────────────────────────

    private fun makeProgrammingLessons(courseId: String) = listOf(
        Lesson("$courseId-l1", courseId, "Introduction & Setup", LessonType.VIDEO, 600, video(0), 1, true, "Get your dev environment ready and understand the course structure."),
        Lesson("$courseId-l2", courseId, "Core Concepts Overview", LessonType.VIDEO, 900, video(1), 2, true, "A high-level tour of the fundamental concepts we will cover."),
        Lesson("$courseId-l3", courseId, "Variables, Types & Expressions", LessonType.VIDEO, 1200, video(2), 3, false, "Understand how data is stored and manipulated."),
        Lesson("$courseId-l4", courseId, "Control Flow", LessonType.VIDEO, 1080, video(3), 4, false, "Branches and loops — the backbone of any algorithm."),
        Lesson("$courseId-l5", courseId, "Functions & Lambdas", LessonType.VIDEO, 1320, video(4), 5, false, "Reusable blocks of logic and functional programming intro."),
        Lesson("$courseId-l6", courseId, "Object-Oriented Design", LessonType.VIDEO, 1500, video(5), 6, false, "Classes, inheritance, and polymorphism explained clearly."),
        Lesson("$courseId-l7", courseId, "Reading: Design Patterns", LessonType.TEXT, 0, "https://refactoring.guru/design-patterns", 7, false, "Study material on the most useful software patterns."),
        Lesson("$courseId-l8", courseId, "Collections & Generics", LessonType.VIDEO, 1140, video(6), 8, false, "Lists, maps, sets and type-safe containers."),
        Lesson("$courseId-l9", courseId, "Error Handling & Testing", LessonType.VIDEO, 1260, video(7), 9, false, "Writing robust, testable code from the ground up."),
        Lesson("$courseId-l10", courseId, "Module Quiz", LessonType.QUIZ, 0, "", 10, false, "Test your understanding before moving on.")
    )

    private fun makeMLLessons(courseId: String) = listOf(
        Lesson("$courseId-l1", courseId, "What is Machine Learning?", LessonType.VIDEO, 720, video(0), 1, true, "Demystify ML — supervised, unsupervised, and reinforcement learning."),
        Lesson("$courseId-l2", courseId, "Python for Data Science", LessonType.VIDEO, 1080, video(1), 2, true, "NumPy, Pandas, and Matplotlib crash course."),
        Lesson("$courseId-l3", courseId, "Linear Regression Deep Dive", LessonType.VIDEO, 1440, video(2), 3, false, "Math and intuition behind the simplest ML model."),
        Lesson("$courseId-l4", courseId, "Classification Algorithms", LessonType.VIDEO, 1320, video(3), 4, false, "Logistic Regression, KNN, Decision Trees."),
        Lesson("$courseId-l5", courseId, "Neural Networks Basics", LessonType.VIDEO, 1800, video(4), 5, false, "Perceptrons, activation functions, and backpropagation."),
        Lesson("$courseId-l6", courseId, "Training & Overfitting", LessonType.VIDEO, 1200, video(5), 6, false, "Bias-variance tradeoff, regularization, and cross-validation."),
        Lesson("$courseId-l7", courseId, "Reading: ML Mathematics", LessonType.TEXT, 0, "https://mml-book.github.io", 7, false, "Free textbook on the maths behind ML."),
        Lesson("$courseId-l8", courseId, "Convolutional Neural Networks", LessonType.VIDEO, 1680, video(6), 8, false, "Image recognition with CNNs and transfer learning."),
        Lesson("$courseId-l9", courseId, "Model Deployment", LessonType.VIDEO, 1140, video(7), 9, false, "From Jupyter notebook to production REST API."),
        Lesson("$courseId-l10", courseId, "Final Quiz", LessonType.QUIZ, 0, "", 10, false, "Assess your ML knowledge.")
    )

    private fun makeDesignLessons(courseId: String) = listOf(
        Lesson("$courseId-l1", courseId, "Design Thinking Fundamentals", LessonType.VIDEO, 660, video(0), 1, true, "The five-stage design thinking process explained."),
        Lesson("$courseId-l2", courseId, "User Research Methods", LessonType.VIDEO, 900, video(1), 2, true, "Interviews, surveys, and usability tests."),
        Lesson("$courseId-l3", courseId, "Wireframing & Prototyping", LessonType.VIDEO, 1200, video(2), 3, false, "From sketches to clickable prototypes in Figma."),
        Lesson("$courseId-l4", courseId, "Typography & Color Theory", LessonType.VIDEO, 1080, video(3), 4, false, "Build visual hierarchy with type and color."),
        Lesson("$courseId-l5", courseId, "Component Design Systems", LessonType.VIDEO, 1320, video(4), 5, false, "Atomic design and scalable UI component libraries."),
        Lesson("$courseId-l6", courseId, "Accessibility (WCAG 2.2)", LessonType.VIDEO, 960, video(5), 6, false, "Designing for users of all abilities."),
        Lesson("$courseId-l7", courseId, "Reading: Material Design", LessonType.TEXT, 0, "https://m3.material.io", 7, false, "Google's open-source design specification."),
        Lesson("$courseId-l8", courseId, "Motion & Microinteractions", LessonType.VIDEO, 780, video(6), 8, false, "Delightful animations that guide users."),
        Lesson("$courseId-l9", courseId, "Portfolio Project", LessonType.INTERACTIVE, 0, "", 9, false, "Design a real-world app from brief to prototype."),
        Lesson("$courseId-l10", courseId, "Design Quiz", LessonType.QUIZ, 0, "", 10, false, "Test your design knowledge.")
    )

    private fun makeBusinessLessons(courseId: String) = listOf(
        Lesson("$courseId-l1", courseId, "Startup Fundamentals", LessonType.VIDEO, 780, video(0), 1, true, "Problem-solution fit and the lean startup methodology."),
        Lesson("$courseId-l2", courseId, "Market Research & Validation", LessonType.VIDEO, 1020, video(1), 2, true, "How to validate your idea before writing code."),
        Lesson("$courseId-l3", courseId, "Business Model Canvas", LessonType.VIDEO, 1200, video(2), 3, false, "Map out your entire business on one page."),
        Lesson("$courseId-l4", courseId, "Financial Modeling", LessonType.VIDEO, 1440, video(3), 4, false, "P&L, cash flow, and unit economics for startups."),
        Lesson("$courseId-l5", courseId, "Fundraising & Pitch Decks", LessonType.VIDEO, 1320, video(4), 5, false, "How to raise a seed round and craft a winning pitch."),
        Lesson("$courseId-l6", courseId, "Go-to-Market Strategy", LessonType.VIDEO, 1080, video(5), 6, false, "Customer acquisition, pricing, and distribution."),
        Lesson("$courseId-l7", courseId, "Reading: Zero to One", LessonType.TEXT, 0, "https://en.wikipedia.org/wiki/Zero_to_One", 7, false, "Peter Thiel's contrarian guide to building startups."),
        Lesson("$courseId-l8", courseId, "Team Building & Culture", LessonType.VIDEO, 900, video(6), 8, false, "Hiring, culture, and management in early stage companies."),
        Lesson("$courseId-l9", courseId, "Scaling & Operations", LessonType.VIDEO, 1260, video(7), 9, false, "Systems and processes that let you grow without chaos."),
        Lesson("$courseId-l10", courseId, "Business Strategy Quiz", LessonType.QUIZ, 0, "", 10, false, "Test your startup knowledge.")
    )

    private fun makeMathLessons(courseId: String) = listOf(
        Lesson("$courseId-l1", courseId, "Limits & Continuity", LessonType.VIDEO, 900, video(0), 1, true, "The foundation of calculus — approaching a value."),
        Lesson("$courseId-l2", courseId, "Derivatives: Rules & Intuition", LessonType.VIDEO, 1200, video(1), 2, true, "Power rule, chain rule, and what derivatives mean."),
        Lesson("$courseId-l3", courseId, "Applications of Derivatives", LessonType.VIDEO, 1080, video(2), 3, false, "Optimization, related rates, and curve sketching."),
        Lesson("$courseId-l4", courseId, "Integrals: Definite & Indefinite", LessonType.VIDEO, 1320, video(3), 4, false, "The anti-derivative and the area under a curve."),
        Lesson("$courseId-l5", courseId, "Integration Techniques", LessonType.VIDEO, 1440, video(4), 5, false, "Substitution, integration by parts, and partial fractions."),
        Lesson("$courseId-l6", courseId, "Sequences & Series", LessonType.VIDEO, 1260, video(5), 6, false, "Convergence, Taylor series, and Fourier basics."),
        Lesson("$courseId-l7", courseId, "Reading: 3Blue1Brown", LessonType.TEXT, 0, "https://www.3blue1brown.com/topics/calculus", 7, false, "Visual essays on calculus from the famous YouTube channel."),
        Lesson("$courseId-l8", courseId, "Multivariable Calculus Intro", LessonType.VIDEO, 1500, video(6), 8, false, "Partial derivatives, gradients, and the Jacobian."),
        Lesson("$courseId-l9", courseId, "Problem Set Review", LessonType.INTERACTIVE, 0, "", 9, false, "Work through curated problems from MIT OpenCourseWare."),
        Lesson("$courseId-l10", courseId, "Calculus Final Quiz", LessonType.QUIZ, 0, "", 10, false, "Prove your calculus mastery.")
    )

    // ─── Courses ─────────────────────────────────────────────────────────────

    val courses: List<Course> = listOf(
        Course(
            id = "course-1",
            title = "Kotlin for Android & Multiplatform",
            description = "Master Kotlin from basics to advanced — coroutines, flows, sealed classes, and Compose Multiplatform. Build production-ready apps targeting Android and iOS from a single codebase.",
            instructor = instructors[1],
            thumbnailUrl = "https://picsum.photos/seed/kotlin/800/450",
            category = CourseCategory.PROGRAMMING,
            difficulty = Difficulty.INTERMEDIATE,
            rating = 4.8,
            enrolledCount = 18420,
            totalDuration = 14400,
            lessons = makeProgrammingLessons("course-1"),
            price = 0.0,
            isFree = true,
            tags = listOf("kotlin", "android", "multiplatform", "compose", "coroutines")
        ),
        Course(
            id = "course-2",
            title = "Machine Learning A–Z with Python",
            description = "A complete ML bootcamp covering linear regression, decision trees, neural networks, CNNs, and model deployment. Includes 20+ hands-on projects using scikit-learn, TensorFlow, and PyTorch.",
            instructor = instructors[0],
            thumbnailUrl = "https://picsum.photos/seed/mlpython/800/450",
            category = CourseCategory.DATA_SCIENCE,
            difficulty = Difficulty.INTERMEDIATE,
            rating = 4.9,
            enrolledCount = 42300,
            totalDuration = 25200,
            lessons = makeMLLessons("course-2"),
            price = 49.99,
            isFree = false,
            tags = listOf("machine learning", "python", "tensorflow", "sklearn", "ai")
        ),
        Course(
            id = "course-3",
            title = "UX/UI Design Masterclass: Zero to Pro",
            description = "Learn every stage of the product design process — user research, wireframing, visual design, prototyping, and handoff — using Figma and industry-standard methodologies.",
            instructor = instructors[2],
            thumbnailUrl = "https://picsum.photos/seed/uxdesign/800/450",
            category = CourseCategory.DESIGN,
            difficulty = Difficulty.BEGINNER,
            rating = 4.7,
            enrolledCount = 11800,
            totalDuration = 18000,
            lessons = makeDesignLessons("course-3"),
            price = 0.0,
            isFree = true,
            tags = listOf("ux", "ui", "figma", "design", "prototype")
        ),
        Course(
            id = "course-4",
            title = "Build a Startup: From Idea to Launch",
            description = "The complete entrepreneur's playbook — validate your idea, create a business model, raise funding, build a team, and execute a go-to-market strategy. Learn from someone who has done it.",
            instructor = instructors[4],
            thumbnailUrl = "https://picsum.photos/seed/startup/800/450",
            category = CourseCategory.BUSINESS,
            difficulty = Difficulty.BEGINNER,
            rating = 4.6,
            enrolledCount = 9200,
            totalDuration = 16200,
            lessons = makeBusinessLessons("course-4"),
            price = 29.99,
            isFree = false,
            tags = listOf("startup", "entrepreneurship", "business model", "pitch", "fundraising")
        ),
        Course(
            id = "course-5",
            title = "Calculus for Engineers & Data Scientists",
            description = "Rigorous yet approachable calculus — limits, derivatives, integrals, and multivariable calculus. Essential mathematics for ML, physics, and engineering applications.",
            instructor = instructors[5],
            thumbnailUrl = "https://picsum.photos/seed/calculus/800/450",
            category = CourseCategory.MATH,
            difficulty = Difficulty.ADVANCED,
            rating = 4.9,
            enrolledCount = 7600,
            totalDuration = 21600,
            lessons = makeMathLessons("course-5"),
            price = 0.0,
            isFree = true,
            tags = listOf("calculus", "mathematics", "derivatives", "integrals", "engineering")
        ),
        Course(
            id = "course-6",
            title = "Data Analysis with SQL & Python",
            description = "Become a data analyst from scratch. Master SQL queries, window functions, joins, and Python pandas to extract insights from real datasets. Includes 15 capstone projects.",
            instructor = instructors[3],
            thumbnailUrl = "https://picsum.photos/seed/dataanalysis/800/450",
            category = CourseCategory.DATA_SCIENCE,
            difficulty = Difficulty.BEGINNER,
            rating = 4.8,
            enrolledCount = 23500,
            totalDuration = 19800,
            lessons = makeProgrammingLessons("course-6"),
            price = 39.99,
            isFree = false,
            tags = listOf("sql", "python", "pandas", "data analysis", "bi")
        ),
        Course(
            id = "course-7",
            title = "React Native: Build Cross-Platform Apps",
            description = "Create iOS and Android apps with React Native and Expo. Covers navigation, state management with Redux Toolkit, REST APIs, push notifications, and App Store deployment.",
            instructor = instructors[1],
            thumbnailUrl = "https://picsum.photos/seed/reactnative/800/450",
            category = CourseCategory.PROGRAMMING,
            difficulty = Difficulty.INTERMEDIATE,
            rating = 4.7,
            enrolledCount = 15600,
            totalDuration = 22500,
            lessons = makeProgrammingLessons("course-7"),
            price = 44.99,
            isFree = false,
            tags = listOf("react native", "javascript", "expo", "ios", "android")
        ),
        Course(
            id = "course-8",
            title = "Advanced Deep Learning & NLP",
            description = "Go beyond the basics — transformers, BERT, GPT architectures, attention mechanisms, and fine-tuning large language models for production NLP tasks.",
            instructor = instructors[0],
            thumbnailUrl = "https://picsum.photos/seed/deeplearning/800/450",
            category = CourseCategory.DATA_SCIENCE,
            difficulty = Difficulty.ADVANCED,
            rating = 4.9,
            enrolledCount = 8900,
            totalDuration = 28800,
            lessons = makeMLLessons("course-8"),
            price = 59.99,
            isFree = false,
            tags = listOf("deep learning", "nlp", "transformers", "gpt", "bert")
        ),
        Course(
            id = "course-9",
            title = "Brand Identity Design",
            description = "Design compelling brand identities from logo creation to full brand guidelines. Learn color psychology, typography pairing, brand voice, and how to present work to clients.",
            instructor = instructors[2],
            thumbnailUrl = "https://picsum.photos/seed/branding/800/450",
            category = CourseCategory.DESIGN,
            difficulty = Difficulty.INTERMEDIATE,
            rating = 4.6,
            enrolledCount = 5400,
            totalDuration = 14400,
            lessons = makeDesignLessons("course-9"),
            price = 34.99,
            isFree = false,
            tags = listOf("branding", "logo", "identity", "graphic design", "figma")
        ),
        Course(
            id = "course-10",
            title = "Spanish for Beginners: Conversational Fast-Track",
            description = "Go from zero to conversational Spanish in 8 weeks. Focus on real-world dialogues, pronunciation, and practical vocabulary used by native speakers — no textbook Spanish.",
            instructor = Instructor("inst-7", "Ana López", "https://i.pravatar.cc/150?img=44", "Native Spanish speaker and language coach with 15 years of experience.", 3, 4.8),
            thumbnailUrl = "https://picsum.photos/seed/spanish/800/450",
            category = CourseCategory.LANGUAGE,
            difficulty = Difficulty.BEGINNER,
            rating = 4.8,
            enrolledCount = 31200,
            totalDuration = 17100,
            lessons = makeBusinessLessons("course-10"),
            price = 0.0,
            isFree = true,
            tags = listOf("spanish", "language", "conversation", "pronunciation", "beginner")
        )
    )

    // ─── Quizzes ─────────────────────────────────────────────────────────────

    val quizzes: Map<String, Quiz> = mapOf(
        "course-1-l10" to Quiz(
            id = "quiz-1",
            lessonId = "course-1-l10",
            title = "Kotlin Fundamentals Quiz",
            questions = listOf(
                QuizQuestion("q1", "Which keyword is used to declare a mutable variable in Kotlin?", listOf("val", "var", "let", "mut"), 1, "'var' declares a mutable variable. 'val' declares an immutable (read-only) variable."),
                QuizQuestion("q2", "What is the output of: println(if (true) \"yes\" else \"no\")?", listOf("yes", "no", "null", "Compile error"), 0, "In Kotlin, 'if' is an expression that returns a value, so this prints 'yes'."),
                QuizQuestion("q3", "Which collection is immutable by default in Kotlin?", listOf("ArrayList", "MutableList", "List", "LinkedList"), 2, "'List' in Kotlin is read-only. To get a mutable list use 'MutableList' or 'mutableListOf()'."),
                QuizQuestion("q4", "What does the 'data class' keyword provide automatically?", listOf("Singleton pattern", "equals, hashCode, copy, toString", "Lazy initialization", "Thread safety"), 1, "Data classes auto-generate equals(), hashCode(), copy(), toString(), and componentN() functions."),
                QuizQuestion("q5", "How do you make a function parameter optional in Kotlin?", listOf("Use ? after the type", "Provide a default value", "Mark it with @Optional", "Use vararg"), 1, "You provide a default value in the function signature: fun foo(x: Int = 0).")
            ),
            passingScore = 70,
            timeLimit = 300
        ),
        "course-2-l10" to Quiz(
            id = "quiz-2",
            lessonId = "course-2-l10",
            title = "Machine Learning Concepts Quiz",
            questions = listOf(
                QuizQuestion("q6", "What does 'overfitting' mean in machine learning?", listOf("Model is too simple", "Model memorises training data and fails on new data", "Dataset is too small", "Loss function is incorrect"), 1, "Overfitting occurs when a model learns noise in training data, performing well on training but poorly on unseen data."),
                QuizQuestion("q7", "Which activation function is commonly used in hidden layers of deep networks?", listOf("Sigmoid", "ReLU", "Tanh", "Softmax"), 1, "ReLU (Rectified Linear Unit) is preferred in hidden layers because it avoids the vanishing gradient problem."),
                QuizQuestion("q8", "What is the purpose of a validation set?", listOf("To train the model", "To tune hyperparameters without touching the test set", "To evaluate final model performance", "To augment training data"), 1, "The validation set is used during training to tune hyperparameters and detect overfitting, keeping the test set pristine."),
                QuizQuestion("q9", "In linear regression, what does the cost function measure?", listOf("Accuracy of classifications", "Mean squared error between predictions and actuals", "Information gain", "Kullback-Leibler divergence"), 1, "MSE measures the average squared difference between predicted and actual values — the most common regression loss."),
                QuizQuestion("q10", "Which algorithm builds an ensemble of decision trees?", listOf("SVM", "Logistic Regression", "Random Forest", "K-Means"), 2, "Random Forest trains many decision trees on random subsets of data and averages their predictions to reduce variance.")
            ),
            passingScore = 80,
            timeLimit = 360
        ),
        "course-3-l10" to Quiz(
            id = "quiz-3",
            lessonId = "course-3-l10",
            title = "UX Design Principles Quiz",
            questions = listOf(
                QuizQuestion("q11", "What is the primary goal of user research?", listOf("Make the UI look good", "Understand user needs, behaviours, and pain points", "Speed up development", "Reduce costs"), 1, "User research grounds design decisions in real human needs rather than assumptions."),
                QuizQuestion("q12", "In Fitts's Law, what two factors determine how easy a target is to click?", listOf("Colour and contrast", "Size and distance", "Shape and position", "Weight and opacity"), 1, "Fitts's Law: time to acquire a target is a function of distance to and size of the target."),
                QuizQuestion("q13", "What does 'affordance' mean in UX design?", listOf("The visual style of a component", "A quality that signals how an object should be used", "Animation timing", "Information architecture"), 1, "Affordances are properties of an object that make clear how it can be interacted with — e.g., a button looks pressable."),
                QuizQuestion("q14", "Which WCAG level is the minimum acceptable for most web products?", listOf("A", "AA", "AAA", "None required"), 1, "WCAG 2.1 Level AA is the widely accepted accessibility standard required by most governments and organisations."),
                QuizQuestion("q15", "What is a 'persona' in design?", listOf("A user who tests the product", "A fictional representative of a user group based on research", "A dark mode theme", "A Figma component"), 1, "Personas are research-based archetypes that help teams empathise with and design for target users.")
            ),
            passingScore = 60,
            timeLimit = null
        )
    )

    // ─── AI-Generated Quiz Templates ─────────────────────────────────────────

    fun generateAiQuiz(topic: String, difficulty: String, questionCount: Int): Quiz {
        val topicLower = topic.lowercase()
        val baseQuestions = when {
            "kotlin" in topicLower || "android" in topicLower -> listOf(
                QuizQuestion("ai-q1", "What is a coroutine in Kotlin?", listOf("A type of thread", "A lightweight concurrency primitive", "A design pattern", "A data structure"), 1, "Coroutines are lightweight concurrency primitives that can be suspended and resumed without blocking threads."),
                QuizQuestion("ai-q2", "What is StateFlow used for?", listOf("Network calls", "A hot observable state holder", "Database queries", "Animation"), 1, "StateFlow is a hot flow that always holds a current value and emits updates to collectors."),
                QuizQuestion("ai-q3", "What does 'sealed class' provide?", listOf("Immutability", "Exhaustive when expressions", "Singleton pattern", "Lazy evaluation"), 1, "Sealed classes restrict the class hierarchy, enabling the compiler to check exhaustiveness in when expressions."),
                QuizQuestion("ai-q4", "What is the difference between launch and async?", listOf("No difference", "launch returns Job, async returns Deferred", "async is blocking", "launch only works on main thread"), 1, "launch fires and forgets (returns Job). async returns a Deferred you can await for a result."),
                QuizQuestion("ai-q5", "Which scope function returns the receiver object?", listOf("let", "run", "apply", "also"), 2, "apply executes a block on the receiver and returns the receiver itself, useful for object configuration.")
            )
            "python" in topicLower || "machine learning" in topicLower || "ml" in topicLower -> listOf(
                QuizQuestion("ai-q1", "What does iloc[] do in pandas?", listOf("Label-based indexing", "Integer-based indexing", "Boolean masking", "Grouping"), 1, "iloc uses integer positions to select rows and columns from a DataFrame."),
                QuizQuestion("ai-q2", "What is gradient descent?", listOf("A loss function", "An optimisation algorithm that minimises loss", "A layer type", "A regularisation method"), 1, "Gradient descent iteratively adjusts model parameters in the direction that reduces the loss function."),
                QuizQuestion("ai-q3", "What does dropout do in neural networks?", listOf("Reduces learning rate", "Randomly zeros neurons during training to prevent overfitting", "Normalises activations", "Initialises weights"), 1, "Dropout randomly deactivates a fraction of neurons each training step, acting as an ensemble method."),
                QuizQuestion("ai-q4", "What is a confusion matrix?", listOf("A hyperparameter grid", "A table of TP, TN, FP, FN predictions", "A loss function", "A data augmentation technique"), 1, "A confusion matrix shows the model's correct and incorrect predictions broken down by class."),
                QuizQuestion("ai-q5", "What does the learning rate control?", listOf("Number of layers", "Step size in gradient descent", "Batch size", "Dropout rate"), 1, "The learning rate determines how large each update step is when adjusting model weights.")
            )
            else -> listOf(
                QuizQuestion("ai-q1", "What is the primary benefit of modular architecture?", listOf("Faster runtime", "Separation of concerns and reusability", "Less code", "Better UI"), 1, "Modular architecture separates concerns, making code easier to test, maintain, and reuse."),
                QuizQuestion("ai-q2", "What does 'DRY' stand for in software development?", listOf("Data Retrieval Yesterday", "Don't Repeat Yourself", "Dynamic Runtime Yield", "Direct Resource Yes"), 1, "DRY (Don't Repeat Yourself) is a principle of reducing repetition of code and logic."),
                QuizQuestion("ai-q3", "What is the purpose of version control?", listOf("Speed up builds", "Track changes and enable collaboration", "Reduce file sizes", "Compile code"), 1, "Version control systems like Git track changes over time and enable multiple developers to collaborate."),
                QuizQuestion("ai-q4", "What is 'technical debt'?", listOf("Software license fees", "Cost of maintaining poor design choices", "Server costs", "Bug bounty programs"), 1, "Technical debt refers to the future cost of reworking code that was written quickly or without best practices."),
                QuizQuestion("ai-q5", "What does 'refactoring' mean?", listOf("Adding new features", "Rewriting code for clarity without changing behaviour", "Deleting old code", "Optimising performance"), 1, "Refactoring improves the internal structure of code without altering its external behaviour.")
            )
        }

        val difficultyLabel = difficulty.lowercase()
        return Quiz(
            id = "ai-quiz-${topic.take(8).replace(" ", "-").lowercase()}",
            lessonId = "ai-generated",
            title = "AI Quiz: $topic (${difficulty.replaceFirstChar { it.uppercase() }})",
            questions = baseQuestions.take(questionCount.coerceIn(1, 5)),
            passingScore = when (difficultyLabel) { "advanced" -> 85; "intermediate" -> 75; else -> 60 },
            timeLimit = when (difficultyLabel) { "advanced" -> 240L; "intermediate" -> 300L; else -> null }
        )
    }

    // ─── Current User ─────────────────────────────────────────────────────────

    val currentUser = User(
        id = "user-1",
        name = "Alex Johnson",
        email = "alex.johnson@example.com",
        avatarUrl = "https://i.pravatar.cc/150?img=3",
        enrolledCourseIds = listOf("course-1", "course-2", "course-3", "course-5", "course-10"),
        completedCourseIds = listOf("course-3"),
        streakDays = 14,
        totalLearningTimeSeconds = 144000,
        certificates = listOf(
            Certificate(
                id = "cert-1",
                courseId = "course-3",
                courseTitle = "UX/UI Design Masterclass: Zero to Pro",
                issuedAt = 1710000000000L,
                imageUrl = "https://picsum.photos/seed/certificate/600/420"
            )
        ),
        preferences = UserPreferences(
            isDarkTheme = false,
            notificationsEnabled = true,
            downloadOverWifiOnly = true,
            playbackSpeed = 1.0f
        )
    )

    // ─── Progress ─────────────────────────────────────────────────────────────

    val progressList = listOf(
        UserProgress(
            userId = "user-1",
            courseId = "course-1",
            completedLessons = listOf("course-1-l1", "course-1-l2", "course-1-l3", "course-1-l4", "course-1-l5"),
            quizScores = mapOf("course-1-l10" to 80),
            lastAccessedLessonId = "course-1-l6",
            overallProgress = 0.5f,
            certificateEarned = false,
            streakDays = 14,
            totalTimeSpentSeconds = 6300
        ),
        UserProgress(
            userId = "user-1",
            courseId = "course-2",
            completedLessons = listOf("course-2-l1", "course-2-l2", "course-2-l3"),
            quizScores = emptyMap(),
            lastAccessedLessonId = "course-2-l4",
            overallProgress = 0.3f,
            certificateEarned = false,
            streakDays = 7,
            totalTimeSpentSeconds = 3960
        ),
        UserProgress(
            userId = "user-1",
            courseId = "course-3",
            completedLessons = listOf("course-3-l1", "course-3-l2", "course-3-l3", "course-3-l4",
                "course-3-l5", "course-3-l6", "course-3-l7", "course-3-l8", "course-3-l9", "course-3-l10"),
            quizScores = mapOf("course-3-l10" to 92),
            lastAccessedLessonId = "course-3-l10",
            overallProgress = 1.0f,
            certificateEarned = true,
            streakDays = 21,
            totalTimeSpentSeconds = 18000
        ),
        UserProgress(
            userId = "user-1",
            courseId = "course-5",
            completedLessons = listOf("course-5-l1", "course-5-l2"),
            quizScores = emptyMap(),
            lastAccessedLessonId = "course-5-l2",
            overallProgress = 0.2f,
            certificateEarned = false,
            streakDays = 3,
            totalTimeSpentSeconds = 2100
        ),
        UserProgress(
            userId = "user-1",
            courseId = "course-10",
            completedLessons = listOf("course-10-l1", "course-10-l2", "course-10-l3", "course-10-l4",
                "course-10-l5", "course-10-l6"),
            quizScores = mapOf("course-10-l10" to 75),
            lastAccessedLessonId = "course-10-l7",
            overallProgress = 0.6f,
            certificateEarned = false,
            streakDays = 10,
            totalTimeSpentSeconds = 10260
        )
    )

    // ─── Notes ────────────────────────────────────────────────────────────────

    val notes = mutableListOf(
        Note(
            id = "note-1",
            userId = "user-1",
            courseId = "course-1",
            lessonId = "course-1-l3",
            content = "val = immutable reference (can still mutate the object), var = mutable reference. Prefer val everywhere!",
            timestampSeconds = 245,
            createdAt = 1711000000000L,
            updatedAt = 1711000000000L
        ),
        Note(
            id = "note-2",
            userId = "user-1",
            courseId = "course-1",
            lessonId = "course-1-l5",
            content = "Higher-order functions: pass lambdas as parameters. Key ones: map, filter, reduce, fold, flatMap.",
            timestampSeconds = 612,
            createdAt = 1711100000000L,
            updatedAt = 1711100000000L
        ),
        Note(
            id = "note-3",
            userId = "user-1",
            courseId = "course-2",
            lessonId = "course-2-l3",
            content = "MSE vs MAE: MSE penalises large errors more (squares them). Use MAE when outliers shouldn't dominate.",
            timestampSeconds = 891,
            createdAt = 1711200000000L,
            updatedAt = 1711250000000L
        ),
        Note(
            id = "note-4",
            userId = "user-1",
            courseId = "course-3",
            lessonId = "course-3-l4",
            content = "60-30-10 colour rule: 60% dominant, 30% secondary, 10% accent. Works for almost every layout.",
            timestampSeconds = 433,
            createdAt = 1711300000000L,
            updatedAt = 1711300000000L
        ),
        Note(
            id = "note-5",
            userId = "user-1",
            courseId = "course-5",
            lessonId = "course-5-l2",
            content = "Chain rule: d/dx[f(g(x))] = f'(g(x)) * g'(x). Always work from outside function inward.",
            timestampSeconds = 1024,
            createdAt = 1711400000000L,
            updatedAt = 1711400000000L
        )
    )

    // ─── Bookmarks ────────────────────────────────────────────────────────────

    val bookmarks = mutableListOf(
        Bookmark(
            id = "bm-1",
            userId = "user-1",
            courseId = "course-1",
            lessonId = "course-1-l6",
            title = "Object-Oriented Design — Resume here",
            createdAt = 1711500000000L
        ),
        Bookmark(
            id = "bm-2",
            userId = "user-1",
            courseId = "course-2",
            lessonId = "course-2-l5",
            title = "Neural Networks — great explanation of backprop",
            createdAt = 1711600000000L
        ),
        Bookmark(
            id = "bm-3",
            userId = "user-1",
            courseId = "course-5",
            lessonId = "course-5-l3",
            title = "Optimization problems — review before exam",
            createdAt = 1711700000000L
        )
    )

    // ─── Downloaded Lessons ───────────────────────────────────────────────────

    val downloads = mutableListOf(
        DownloadedLesson(
            lessonId = "course-1-l1",
            courseId = "course-1",
            title = "Introduction & Setup",
            localFilePath = "/data/user/0/com.learnpulse.android/files/downloads/course-1-l1.mp4",
            fileSizeBytes = 142_000_000,
            downloadedAt = 1711800000000L,
            status = DownloadStatus.COMPLETED
        ),
        DownloadedLesson(
            lessonId = "course-1-l2",
            courseId = "course-1",
            title = "Core Concepts Overview",
            localFilePath = "/data/user/0/com.learnpulse.android/files/downloads/course-1-l2.mp4",
            fileSizeBytes = 198_000_000,
            downloadedAt = 1711850000000L,
            status = DownloadStatus.COMPLETED
        ),
        DownloadedLesson(
            lessonId = "course-3-l1",
            courseId = "course-3",
            title = "Design Thinking Fundamentals",
            localFilePath = "/data/user/0/com.learnpulse.android/files/downloads/course-3-l1.mp4",
            fileSizeBytes = 115_000_000,
            downloadedAt = 1711900000000L,
            status = DownloadStatus.COMPLETED
        ),
        DownloadedLesson(
            lessonId = "course-2-l1",
            courseId = "course-2",
            title = "What is Machine Learning?",
            localFilePath = "/data/user/0/com.learnpulse.android/files/downloads/course-2-l1.mp4",
            fileSizeBytes = 0,
            downloadedAt = 0L,
            status = DownloadStatus.IN_PROGRESS
        )
    )

    // ─── Reviews ─────────────────────────────────────────────────────────────

    val reviews: Map<String, List<CourseReview>> = mapOf(
        "course-1" to listOf(
            CourseReview("rev-1", "course-1", "user-2", "Maria G.", "https://i.pravatar.cc/150?img=9", 5, "Best Kotlin course I've found. Marcus explains concepts with amazing clarity and the projects are real-world.", 1710100000000L),
            CourseReview("rev-2", "course-1", "user-3", "Tom H.", "https://i.pravatar.cc/150?img=15", 5, "Went from zero to publishing my first KMP app in 6 weeks. Absolutely worth it.", 1710200000000L),
            CourseReview("rev-3", "course-1", "user-4", "Aisha K.", "https://i.pravatar.cc/150?img=29", 4, "Great content, though the coroutines section could use more examples. Overall highly recommended.", 1710300000000L)
        ),
        "course-2" to listOf(
            CourseReview("rev-4", "course-2", "user-5", "Raj P.", "https://i.pravatar.cc/150?img=52", 5, "Dr. Chen is exceptional. Complex ML concepts explained with perfect intuition and backed by solid math.", 1710400000000L),
            CourseReview("rev-5", "course-2", "user-6", "Sophie L.", "https://i.pravatar.cc/150?img=36", 5, "Got my first data scientist role after completing this course. The projects section is gold.", 1710500000000L)
        ),
        "course-3" to listOf(
            CourseReview("rev-6", "course-3", "user-7", "Carlos M.", "https://i.pravatar.cc/150?img=60", 5, "Priya's teaching style is incredible. I redesigned my entire portfolio based on what I learned here.", 1710600000000L),
            CourseReview("rev-7", "course-3", "user-8", "Yuki T.", "https://i.pravatar.cc/150?img=45", 4, "Very comprehensive. The Figma walkthroughs are detailed and easy to follow along.", 1710700000000L)
        )
    )
}