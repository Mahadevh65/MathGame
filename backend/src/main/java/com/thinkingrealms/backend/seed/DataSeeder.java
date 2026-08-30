package com.thinkingrealms.backend.seed;

import com.thinkingrealms.backend.domain.*;
import com.thinkingrealms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Seeds the MVP content described in the architecture spec:
 * topics (Numbers, Arithmetic, Fractions, Percentages, Basic Algebra),
 * 6 thinking skills, ~18 questions tagged with both, one game world
 * (The Thinking Realms) with the Number Forest region, three missions
 * (including one boss), and two starter achievements.
 *
 * Runs once: it no-ops if MathTopics already exist, so it is safe on
 * every application restart.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MathTopicRepository mathTopicRepository;
    private final ThinkingSkillRepository thinkingSkillRepository;
    private final QuestionRepository questionRepository;
    private final GameWorldRepository gameWorldRepository;
    private final GameRegionRepository gameRegionRepository;
    private final MissionRepository missionRepository;
    private final AchievementRepository achievementRepository;

    @Override
    public void run(String... args) {
        if (mathTopicRepository.count() > 0) {
            return; // already seeded
        }

        Map<String, MathTopic> topics = seedTopics();
        seedThinkingSkills();

        List<Question> numberForestQuestions = seedQuestions(topics);

        GameWorld world = new GameWorld();
        world.setSlug("thinking-realms");
        world.setName("The Thinking Realms");
        world.setDescription("A mysterious world where mathematical knowledge unlocks new regions.");
        gameWorldRepository.save(world);

        GameRegion numberForest = new GameRegion();
        numberForest.setWorld(world);
        numberForest.setSlug("number-forest");
        numberForest.setName("Number Forest");
        numberForest.setDescription("Where numbers, arithmetic and fractions first come alive.");
        numberForest.setOrderIndex(0);
        numberForest.setUnlockXpThreshold(0);
        numberForest.setThemeSlug("forest");
        gameRegionRepository.save(numberForest);

        GameRegion logicCity = new GameRegion();
        logicCity.setWorld(world);
        logicCity.setSlug("logic-city");
        logicCity.setName("Logic City");
        logicCity.setDescription("A city of gates and puzzles, unlocked once your reasoning sharpens.");
        logicCity.setOrderIndex(1);
        logicCity.setUnlockXpThreshold(150);
        logicCity.setThemeSlug("city");
        gameRegionRepository.save(logicCity);

        // Mission 1: The Lost Numbers (numbers + arithmetic, easier)
        Mission mission1 = new Mission();
        mission1.setRegion(numberForest);
        mission1.setName("The Lost Numbers");
        mission1.setDescription("Recover the scattered numbers of the forest through basic calculation and pattern spotting.");
        mission1.setOrderIndex(0);
        mission1.setBoss(false);
        mission1.setQuestionIds(idsOf(numberForestQuestions, 0, 5));
        missionRepository.save(mission1);

        // Mission 2: The Multiplication Bridge (arithmetic + fractions + percentages)
        Mission mission2 = new Mission();
        mission2.setRegion(numberForest);
        mission2.setName("The Multiplication Bridge");
        mission2.setDescription("Cross the bridge by mastering multiplication strategy, fractions and percentages.");
        mission2.setOrderIndex(1);
        mission2.setBoss(false);
        mission2.setQuestionIds(idsOf(numberForestQuestions, 5, 11));
        missionRepository.save(mission2);

        // Boss: The Number Guardian (mixed, harder, includes basic algebra + constraint problem)
        Mission boss = new Mission();
        boss.setRegion(numberForest);
        boss.setName("The Number Guardian");
        boss.setDescription("A multi-skill reasoning challenge guarding the way to Logic City.");
        boss.setOrderIndex(2);
        boss.setBoss(true);
        boss.setQuestionIds(idsOf(numberForestQuestions, 11, numberForestQuestions.size()));
        missionRepository.save(boss);

        seedAchievements();
    }

    private List<UUID> idsOf(List<Question> questions, int fromInclusive, int toExclusive) {
        return questions.subList(fromInclusive, toExclusive).stream().map(Question::getId).toList();
    }

    private Map<String, MathTopic> seedTopics() {
        MathTopic numbers = topic("numbers", "Numbers", "Counting, place value, comparison.", 0);
        MathTopic arithmetic = topic("arithmetic", "Arithmetic", "Addition, subtraction, multiplication, division.", 1);
        MathTopic fractions = topic("fractions", "Fractions", "Parts of a whole, equivalence, operations.", 2);
        MathTopic percentages = topic("percentages", "Percentages", "Percent as a fraction of 100 and its applications.", 3);
        MathTopic algebra = topic("basic-algebra", "Basic Algebra", "Variables, expressions, simple equations.", 4);

        return Map.of(
                "numbers", numbers,
                "arithmetic", arithmetic,
                "fractions", fractions,
                "percentages", percentages,
                "basic-algebra", algebra
        );
    }

    private MathTopic topic(String slug, String name, String description, int order) {
        MathTopic t = new MathTopic();
        t.setSlug(slug);
        t.setName(name);
        t.setDescription(description);
        t.setOrderIndex(order);
        return mathTopicRepository.save(t);
    }

    private void seedThinkingSkills() {
        skill("pattern-recognition", "Pattern Recognition", "Spotting structure and regularity in numbers and shapes.");
        skill("logical-reasoning", "Logical Reasoning", "Drawing valid conclusions from given information.");
        skill("estimation", "Estimation", "Judging an approximate answer before or instead of exact calculation.");
        skill("problem-decomposition", "Problem Decomposition", "Breaking a complex problem into smaller, solvable parts.");
        skill("strategy-selection", "Strategy Selection", "Choosing an efficient method before diving into calculation.");
        skill("error-detection", "Error Detection", "Identifying where and why a solution goes wrong.");
    }

    private void skill(String slug, String name, String description) {
        ThinkingSkill s = new ThinkingSkill();
        s.setSlug(slug);
        s.setName(name);
        s.setDescription(description);
        thinkingSkillRepository.save(s);
    }

    private void seedAchievements() {
        achievement("first_correct_answer", "First Steps", "Answered your first question correctly.");
        achievement("five_streak", "On a Roll", "Answered five questions correctly in a row.");
    }

    private void achievement(String code, String name, String description) {
        Achievement a = new Achievement();
        a.setCode(code);
        a.setName(name);
        a.setDescription(description);
        achievementRepository.save(a);
    }

    private List<Question> seedQuestions(Map<String, MathTopic> topics) {
        Question q1 = question(
                "Order these from smallest to largest: 402, 420, 240.",
                QuestionType.STANDARD_CALCULATION, 1, topics.get("numbers"),
                List.of("pattern-recognition", "logical-reasoning"),
                "240, 402, 420",
                "Compare the hundreds digit first, then the tens digit.",
                List.of("Look at the hundreds digit of each number first.", "240 has 2 hundreds; the others have 4.", "Between 402 and 420, compare the tens digit."),
                "Comparing digit-by-digit from the left avoids relying only on 'more digits = bigger'.",
                12, 45
        );

        Question q2 = question(
                "What is 356 + 278?",
                QuestionType.STANDARD_CALCULATION, 2, topics.get("arithmetic"),
                List.of("estimation", "strategy-selection"),
                "634",
                "Add the ones, tens, and hundreds, carrying where needed.",
                List.of("Try estimating first: 356 is close to 360, 278 is close to 280.", "360 + 280 = 640, so the exact answer should be close to that.", "Add column by column, carrying the 1 where the sum exceeds 9."),
                "A common mistake is forgetting to carry the 1 when a column sums past 9.",
                10, 40
        );

        Question q3 = question(
                "Continue the pattern: 2, 6, 12, 20, 30, ?",
                QuestionType.PATTERN_RECOGNITION, 3, topics.get("numbers"),
                List.of("pattern-recognition", "logical-reasoning"),
                "42",
                "Each term is n(n+1): 1x2, 2x3, 3x4, 4x5, 5x6, 6x7=42.",
                List.of("Look at the differences between consecutive terms: 4, 6, 8, 10...", "The differences themselves increase by 2 each time.", "The next difference should be 12, so add 12 to 30."),
                "Students often assume a constant difference (like 8) instead of checking whether the difference itself changes.",
                15, 60
        );

        Question q4 = question(
                "A student solved 15 - 8 and got 6. Is this correct? If not, what's the right answer?",
                QuestionType.FIND_THE_MISTAKE, 2, topics.get("arithmetic"),
                List.of("error-detection", "logical-reasoning"),
                "7",
                "15 - 8 = 7. The student likely miscounted by one when borrowing.",
                List.of("Try counting up from 8 to 15 on your fingers or a number line.", "8 + 7 = 15, so 15 - 8 should equal 7."),
                "Off-by-one errors are common when subtracting without a clear method like counting up or borrowing carefully.",
                14, 40
        );

        Question q5 = question(
                "Estimate: 48 x 22 is closest to which of these? (Type your estimate as a number.)",
                QuestionType.ESTIMATION, 2, topics.get("arithmetic"),
                List.of("estimation", "strategy-selection"),
                "1000",
                "Round 48 to 50 and 22 to 20: 50 x 20 = 1000. The exact answer, 1056, is close to this.",
                List.of("Round both numbers to the nearest ten.", "50 x 20 is an easy multiplication.", "This gives a quick, close estimate without long multiplication."),
                "Estimating before calculating helps catch large errors in the exact calculation later.",
                12, 45
        );

        Question q6 = question(
                "What is 99 x 25? Try to find a fast strategy rather than long multiplication.",
                QuestionType.STRATEGY_SELECTION, 4, topics.get("arithmetic"),
                List.of("strategy-selection", "estimation"),
                "2475",
                "99 x 25 = (100 x 25) - 25 = 2500 - 25 = 2475.",
                List.of("Notice 99 is very close to 100.", "Calculate 100 x 25 first — that's easy.", "Then subtract one group of 25, since 99 is one less than 100."),
                "Using long multiplication here works but is slower and more error-prone than adjusting from a nearby round number.",
                18, 50
        );

        Question q7 = question(
                "What is 3/4 of 20?",
                QuestionType.STANDARD_CALCULATION, 3, topics.get("fractions"),
                List.of("problem-decomposition", "strategy-selection"),
                "15",
                "Divide 20 by 4 to find one quarter (5), then multiply by 3.",
                List.of("Break the problem into two steps: find 1/4 first.", "20 divided by 4 is 5.", "Since you want 3/4, multiply that result by 3."),
                "A common error is multiplying 20 by 3 and then trying to divide by 4 in the wrong order, or forgetting to divide at all.",
                14, 45
        );

        Question q8 = question(
                "Add these fractions: 1/2 + 1/4",
                QuestionType.STANDARD_CALCULATION, 3, topics.get("fractions"),
                List.of("strategy-selection", "problem-decomposition"),
                "3/4",
                "Convert 1/2 to 2/4, then add: 2/4 + 1/4 = 3/4.",
                List.of("You need a common denominator before adding fractions.", "4 works as a common denominator for both 1/2 and 1/4.", "Convert 1/2 into fourths, then add the numerators."),
                "Adding numerators and denominators directly (getting 2/6) is the most common fraction-addition mistake.",
                14, 45
        );

        Question q9 = question(
                "A shirt costs $40 and is on sale for 25% off. What is the sale price?",
                QuestionType.REAL_WORLD, 4, topics.get("percentages"),
                List.of("problem-decomposition", "strategy-selection"),
                "30",
                "25% of 40 is 10. Subtract the discount from the original price: 40 - 10 = 30.",
                List.of("First find what 25% of $40 is.", "25% is the same as 1/4, so divide 40 by 4.", "Subtract that discount amount from the original price."),
                "Some students calculate the discount but forget to subtract it from the original price, or subtract the wrong amount.",
                16, 50
        );

        Question q10 = question(
                "0.5, 1/2 and 50% all represent the same value. Which of these equals 3/5 as a decimal?",
                QuestionType.STANDARD_CALCULATION, 4, topics.get("percentages"),
                List.of("logical-reasoning", "strategy-selection"),
                "0.6",
                "3/5 = 6/10 = 0.6.",
                List.of("Try converting the fraction to tenths.", "5 fits into 10 twice, so multiply numerator and denominator by 2.", "3/5 becomes 6/10, which is 0.6."),
                "Dividing incorrectly (e.g. getting 0.35 by misreading the fraction) is a common slip here.",
                14, 45
        );

        Question q11 = question(
                "If x + 5 = 12, what is x?",
                QuestionType.STANDARD_CALCULATION, 3, topics.get("basic-algebra"),
                List.of("logical-reasoning", "strategy-selection"),
                "7",
                "Subtract 5 from both sides: x = 12 - 5 = 7.",
                List.of("Whatever you do to one side, you must do to the other.", "Subtract 5 from both sides of the equation.", "This isolates x on one side."),
                "Forgetting to apply the same operation to both sides is the classic early-algebra mistake.",
                14, 45
        );

        Question q12 = question(
                "Which strategy would you use FIRST to solve: 2(x + 3) = 16 ?",
                QuestionType.STRATEGY_SELECTION, 5, topics.get("basic-algebra"),
                List.of("strategy-selection", "problem-decomposition"),
                "distribute",
                "Distributing the 2 first (2x + 6 = 16) is usually cleaner than dividing both sides by 2 immediately, though both work.",
                List.of("You could distribute the 2 across (x + 3), or divide both sides by 2 first — both are valid.", "Try distributing: 2(x+3) becomes 2x + 6.", "Then isolate x as usual."),
                "There is more than one valid strategy here; the goal is recognizing and justifying a choice, not finding a single 'correct' method.",
                18, 60
        );

        Question q13 = question(
                "A student says: 2(x + 3) = 2x + 3. Is this correct or incorrect? Answer 'correct' or 'incorrect'.",
                QuestionType.FIND_THE_MISTAKE, 4, topics.get("basic-algebra"),
                List.of("error-detection", "logical-reasoning"),
                "incorrect",
                "The distributive property requires multiplying BOTH terms inside the parentheses: 2(x+3) = 2x + 6, not 2x + 3.",
                List.of("Check whether both terms inside the parentheses were multiplied by 2.", "2 times x is 2x. 2 times 3 is 6, not 3.", "The correct expansion is 2x + 6."),
                "This is one of the most common distributive-property errors: multiplying only the first term.",
                18, 50
        );

        Question q14 = question(
                "You have 100 energy points. Stage A costs 20, Stage B costs 35, Stage C costs 15. What is the MAXIMUM number of stages you can complete without repeating a stage?",
                QuestionType.CONSTRAINT, 6, topics.get("arithmetic"),
                List.of("problem-decomposition", "strategy-selection", "logical-reasoning"),
                "3",
                "20 + 35 + 15 = 70, which is within the 100-point budget, so all three stages (the maximum possible, since there are only three) can be completed.",
                List.of("Add up the cost of all available stages first.", "20 + 35 + 15 = 70.", "Since 70 is less than or equal to 100, every stage is affordable."),
                "Some students stop after finding one valid combination instead of checking whether ALL stages fit the budget.",
                25, 90
        );

        Question q15 = question(
                "Explain: why do we divide both sides of an equation by the same number to isolate a variable?",
                QuestionType.EXPLAIN_REASONING, 5, topics.get("basic-algebra"),
                List.of("logical-reasoning"),
                "balance",
                "An equation is a balance — whatever operation you perform on one side, you must perform on the other to keep both sides equal.",
                List.of("Think of the equation like a balanced scale.", "If you remove weight from one side without removing the same from the other, it tips.", "Dividing both sides by the same number keeps the equation balanced while simplifying it."),
                "Students who don't grasp the 'balance' idea often apply operations to only one side.",
                20, 60
        );

        Question q16 = question(
                "A rectangle's perimeter is 100 energy points to build entirely from fencing that costs 1 point per unit length. If the length is 30, what is the width?",
                QuestionType.UNFAMILIAR_TRANSFER, 6, topics.get("basic-algebra"),
                List.of("problem-decomposition", "strategy-selection", "logical-reasoning"),
                "20",
                "Perimeter = 2(length + width). 100 = 2(30 + width) -> 50 = 30 + width -> width = 20.",
                List.of("Recall the perimeter formula for a rectangle: 2 x (length + width).", "Substitute the known perimeter and length into the formula.", "Solve the resulting equation for the width, the same way you solved x + 5 = 12 earlier."),
                "This question deliberately reuses the equation-solving skill from an earlier, more familiar algebra question in a new context.",
                22, 75
        );

        return questionRepository.saveAll(List.of(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16));
    }

    private Question question(
            String text, QuestionType type, int difficulty, MathTopic topic,
            List<String> thinkingSkillSlugs, String correctAnswer, String explanation,
            List<String> hints, String commonMistakeNote, int xpReward, int expectedTimeSeconds
    ) {
        Question q = new Question();
        q.setQuestionText(text);
        q.setQuestionType(type);
        q.setDifficulty(difficulty);
        q.setMathTopic(topic);
        q.setThinkingSkillSlugs(thinkingSkillSlugs);
        q.setCorrectAnswer(correctAnswer);
        q.setExplanation(explanation);
        q.setHints(hints);
        q.setCommonMistakeNote(commonMistakeNote);
        q.setXpReward(xpReward);
        q.setExpectedTimeSeconds(expectedTimeSeconds);
        return q;
    }
}
