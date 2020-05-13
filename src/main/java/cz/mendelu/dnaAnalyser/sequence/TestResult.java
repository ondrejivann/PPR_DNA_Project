package cz.mendelu.dnaAnalyser.sequence;

import cz.mendelu.dnaAnalyser.sequence.stream.Window;

public class TestResult {

    private String sequence;
    private Integer position;
    private Integer length;
    private Double score;
    private String model;

    private String niceSequence;

    public TestResult(Window window) {
        this.sequence = window.toPlain();
        this.position = window.getPosition();
        this.length = window.getSize();
        this.model = "";
        this.niceSequence ="";
        this.score = countScore(window);
    }

    private Double countScore(Window window) {
        Double score = 0.0;

        int length = window.getSize();

        byte cCounter = 0;
        byte spaces = 0;


        for (int i = 0; i < length; i++){


            Nucleotide nucleotide = window.get(i);
            Nucleotide nextNucleotide;
            if (length -1 < i) {
                 nextNucleotide = window.get(i+1);
            }



            if (nucleotide == Nucleotide.C && cCounter > 0 && i < length-1 && window.get(i+1) == Nucleotide.C ){
                cCounter += 1;
                niceSequence = niceSequence.concat(Character.toString(sequence.charAt(i)));

                if (spaces != 0) {
                    niceSequence = niceSequence.concat("-");

                    score += countSpaceBonus(spaces);
                }
                spaces = 0;
            } else if (nucleotide == Nucleotide.C) {
                niceSequence = niceSequence.concat(Character.toString(sequence.charAt(i)));

                cCounter += 1;
                if (spaces > 0) {
                    spaces += 1;
                }

            } else {

                spaces += 1;
                if (cCounter > 2) {
                    niceSequence = niceSequence.concat("-");

                    score += countCBonus(cCounter);
                    addLengthOf(cCounter);
                }

                niceSequence = niceSequence.concat(Character.toString(sequence.charAt(i)));

                cCounter = 0;
            }


        }



        if (cCounter!= 0) {
            score += countCBonus(cCounter);
            addLengthOf(cCounter);
        }

        if (spaces!= 0) {
            score += countSpaceBonus(spaces);
        }

        return score;
    }

    private void addLengthOf(int countOfC) {
        if (this.model.isEmpty())
            this.model += countOfC;
        else
            this.model += "-" + countOfC;
    }

    public Double getScore() {
        return score;
    }

    public String getModel() {
        return model;
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


    @Override
    public String toString() {
        return "Result{" +
                "sequence = '" + niceSequence + '\'' +
                ", position = " + position +
                ", length = " + length +
                ", score = " +  Math.round(score * Math.pow(10, 2)) / Math.pow(10, 2) +
                ", model = '" + model + '\'' +
                '}';
    }
}

