package cz.mendelu.dnaAnalyser.sequence;
import cz.mendelu.dnaAnalyser.sequence.stream.Window;

public class Result {

    private String sequence;
    private String niceSequence;
    private Integer startPosition;
    private Integer endPosition;
    private Integer length;
    private Double score;
    private String model;
    private Integer cSequences;
    private Integer totalSpaces;

    private Boolean startsWithCSequence;
    private Window window;


    public Result(Window window) {
        this.sequence = window.toPlain();
        this.niceSequence = "";
        this.startPosition = window.getPosition();
        this.length = window.getSize();
        this.model = "";
        this.totalSpaces = 0;
        this.cSequences = 0;
        this.startsWithCSequence = (window.get(0) == Nucleotide.C && window.get(1) == Nucleotide.C);

        this.window = window;
    }

    public void setScore() {
        this.score = countScore(this.window);
    }

    public Double countScore(Window window) {
        Double score = 0.0;

        int length = window.getSize();

        byte cCounter = 0;
        byte tempSpaces = 0;
        byte spaceSequences = 0;

        for (int i = 0; i < length; i++){
            endPosition = i;

            Nucleotide nucleotide = window.get(i);
            Nucleotide nextNucleotide = getNextNucleotide(window, i, length);

            //Is it in the middle of sequence?
            if (nextNucleotide != null) {
                //Is it in the middle of C-sequence?
                if (nucleotide == Nucleotide.C && nextNucleotide == Nucleotide.C){
                    cCounter += 1;
                    if (cCounter == 1) {
                        cSequences += 1;
                    }
                    if (tempSpaces > 0) {
                        niceSequence = niceSequence.concat("-");
                        score += countSpaceBonus(tempSpaces);
                    }
                    tempSpaces = 0;
                //Is it in the end of C-sequence?
                } else if (nucleotide == Nucleotide.C && tempSpaces == 0) {
                    cCounter += 1;
                //Is it somewhere between C-sequences?
                } else {
                    tempSpaces += 1;
                    this.totalSpaces += 1;

                    if (tempSpaces == 1) {
                        spaceSequences++;

                        if (spaceSequences == 4) {
                            if (cCounter > 1) {
                                score += countCBonus(cCounter);
                                addLengthOf(cCounter);
                                this.totalSpaces -= 1;

                                return score;
                            }
                        }
                    }
                    if (cCounter > 1) {
                        niceSequence = niceSequence.concat("-");
                        score += countCBonus(cCounter);
                        addLengthOf(cCounter);
                    }
                    cCounter = 0;
                }
            //Last Nucleotide in a whole sequence
            } else {
                if (nucleotide == Nucleotide.C){
                    cCounter += 1;
                } else {
                    tempSpaces += 1;
                    this.totalSpaces += 1;
                    if (cCounter > 1) {
                        spaceSequences++;
                        score += countCBonus(cCounter);
                        addLengthOf(cCounter);

                        if (spaceSequences < 4) {
                            niceSequence = niceSequence.concat("-");

                        } else {
                            totalSpaces -= 1;
                            return score;
                        }

                    }

                    cCounter = 0;
                }
            }

            niceSequence = niceSequence.concat(Character.toString(sequence.charAt(i)));


        }

        if (cCounter!= 0) {
            score += countCBonus(cCounter);
            addLengthOf(cCounter);
        }

//        if (tempSpaces!= 0) {
//            score += countSpaceBonus(tempSpaces);
//        }

        return score;
    }

    private Double countCBonus(int cCounter) {
        return cCounter - 1.0;
    }

    private Double countSpaceBonus(int space) {
        switch (space){
            case 0:
                return 0.00;
            case 1:
                return 0.33;
            case 2:
                return 0.66;
            case 3:
                return 0.66;
            case 4:
                return 0.61;
            case 5:
                return 0.57;
            case 6:
                return 0.52;
            case 7:
                return 0.47;
            case 8:
                return 0.42;
            case 9:
                return 0.38;
            case 10:
                return 0.33;
            case 11:
                return 0.32;
            case 12:
                return 0.30;
            case 13:
                return 0.29;
            case 14:
                return 0.28;
            case 15:
                return 0.27;
            case 16:
                return 0.25;
            case 17:
                return 0.24;
            case 18:
                return 0.23;
            case 19:
                return 0.22;
            case 20:
                return 0.20;
            case 21:
                return 0.19;
            case 22:
                return 0.18;
            case 23:
                return 0.17;
            case 24:
                return 0.15;
            case 25:
                return 0.14;
            case 26:
                return 0.13;
            case 27:
                return 0.11;
            case 28:
                return 0.10;
            case 29:
                return 0.09;
            case 30:
                return 0.08;
            case 31:
                return 0.06;
            case 32:
                return 0.05;
            case 33:
                return 0.04;
            case 34:
                return 0.03;
            case 35:
                return 0.01;
            case 36:
                return 0.0;
            default:
                return 0.0;
        }
    }

    private Nucleotide getNextNucleotide(Window window, int i, int length) {
        Nucleotide nucleotide = null;

        if (i < length - 1) {
            nucleotide = window.get(i+1);
        }

        return nucleotide;
    }

    private void addLengthOf(int countOfC) {
        if (this.model.isEmpty())
            this.model += countOfC;
        else
            this.model += "-" + countOfC;
    }


    public String getSequence() {
        return sequence;
    }

    public String getNiceSequence() {
        return niceSequence;
    }

    public Double getScore() {
        return score;
    }

    public String getModel() {
        return model;
    }

    public Integer getCSequences() {
        return cSequences;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public Boolean startsWithCSequence() {
        return startsWithCSequence;
    }

    public Boolean isIMotif() {
        return
                (
                        this.cSequences == 4
                        && this.getScore() >= 5
                        && this.totalSpaces <= 36
                );
    }

    @Override
    public String toString() {
        return "Result{" +
                "sequence = '" + niceSequence + '\'' +
                ", position = " + startPosition +
                ", length = " + length +
                ", score = " + Math.round(score * Math.pow(10, 2)) / Math.pow(10, 2) +
                ", model = '" + model + '\'' +
                ", spaces = " + totalSpaces +
                ", C-sequences = " + cSequences +
                '}';
    }
}

