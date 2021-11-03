package h01;

import org.sourcegrade.jagr.api.rubric.*;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;
import org.sourcegrade.insnreplacer.ReplacementTransformerKt;

import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;

@RubricForSubmission("h01")
public class H01_RubricProvider implements RubricProvider {
  /*---------------------------------- H1 ------------------------------------*/
  public static final Criterion H1_T1 = Criterion.builder()
    .shortDescription("Zu Beginn hat der Turm zwischen 12 und 20 Münzen und der Läufer keine Münzen.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T1")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H1_T2 = Criterion.builder()
    .shortDescription("Die Startpositionen der Roboter wurden korrekt zufällig ermittelt.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T2")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H1_T3 = Criterion.builder()
    .shortDescription("Die Ausrichtung der Roboter wurde korrekt zufällig ermittelt.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T3")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H1_correct = Criterion.builder()
    .shortDescription("Die Initialisierung funktioniert korrekt.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T1")))
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T2")))
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H1_T3")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_1_T1 = Criterion.builder()
    .shortDescription("Der Turm macht mindestens eine Aktion.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T1")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_1_T2 = Criterion.builder()
    .shortDescription("Der Turm alterniert korrekt zwischen Münze ablegen und laufen.")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T2")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_1_T3 = Criterion.builder()
    .shortDescription("Der Turm dreht sich nach der erwarten Wahrscheinlichkeitsverteilung")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T3")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_1_correct = Criterion.builder()
    .shortDescription("Der Turm verhält sich so wie erwartet")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T1"))).pointsPassedMax()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T2"))).pointsPassedMax()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_1_T3"))).pointsPassedMax()
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_2_T1 = Criterion.builder()
    .shortDescription("Der Läufer macht mindestens eine Aktion und bewegt sich abwechselnd mit dem Turm")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_2_T1")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_2_T2 = Criterion.builder()
    .shortDescription("Der Läufer bewegt sich diagonal, wie gefordert")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_2_T2")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_2_T3 = Criterion.builder()
    .shortDescription("Der Läufer verhält sich so wie erwartet, einschließlich dem Einsammeln von Münzen und daraufhin anhalten")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_2_T3")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_3_T1 = Criterion.builder()
    .shortDescription("Die Ausführung bricht ab wenn Läufer und Turm auf einem Feld landen")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_3_T1")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_3_T2 = Criterion.builder()
    .shortDescription("Die Ausführung bricht ab wenn der Turm keine Münzen mehr hat")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_3_T2")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H3_3_T3 = Criterion.builder()
    .shortDescription("Die Konsolenausgaben entsprechen der Aufgabenstellung")
    .grader(Grader.testAwareBuilder()
      .requirePass(JUnitTestRef.ofMethod(() ->
        TutorTests.class.getMethod("H3_3_T3")))
      .pointsPassedMax()
      .pointsFailedMin()
      .build()
    ).build();

  public static final Criterion H1 = Criterion.builder()
    .shortDescription("H1 – Initialisieren vor der Hauptschleife")
    .addChildCriteria(
      H1_T1,
      H1_T2,
      H1_T3,
      H1_correct
    )
    .build();

  public static final Criterion H3_1 = Criterion.builder()
    .shortDescription("H3.1 – Bewegung von Rook")
    .addChildCriteria(
      H3_1_T1,
      H3_1_T2,
      H3_1_T3,
      H3_1_correct
    )
    .build();

  public static final Criterion H3_2 = Criterion.builder()
    .shortDescription("H3.2 – Bewegung von Bishop")
    .addChildCriteria(
      H3_2_T1,
      H3_2_T2,
      H3_2_T3
    )
    .build();

  public static final Criterion H3_3 = Criterion.builder()
    .shortDescription("H3.3 – Beendigung der Hauptschleife")
    .addChildCriteria(
      H3_3_T1,
      H3_3_T2,
      H3_3_T3
    )
    .build();

  public static final Rubric RUBRIC = Rubric.builder()
    .title("h01")
    .addChildCriteria(H1, H3_1, H3_2, H3_3)
    .build();

  @Override
  public Rubric getRubric() {
    return RUBRIC;
  }

  @Override
  public void configure(RubricConfiguration configuration) {
    configuration.addTransformer(ReplacementTransformerKt.create(ThreadLocalRandomTester.class, ThreadLocalRandom.class));
    configuration.addTransformer(ReplacementTransformerKt.create(JOptionPaneTester.class, JOptionPane.class));
  }
}
