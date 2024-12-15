package Software;

import Devices.CardReader;

public class User implements Runnable {
    private final CardReader cardReader;

    private volatile boolean isRunning;

    private char userType;

    /**
     * Constructor for the User class.
     * 
     * @param cardReader the card reader
     */
    public User(CardReader cardReader) {
        this.cardReader = cardReader;
        this.userType = 'O';
        this.isRunning = true;
    }

    /**
     * Returns the character/user type
     * Will be either 'A', 'V', or 'O'
     * 
     * @return char this.userType
     */
    public char getUserType() {
        return this.userType;
    }

    /**
     * Used by VoteManager to force the CardReader to eject the card
     */
    public void ejectCard() {
        this.userType = 'O';
        this.cardReader.ejectCard();
    }

    /**
     * Turn on the user and start a new thread to simulate the user.
     */
    @Override
    public void run() {
        try {
            while (this.isRunning) {
                processUserCode();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("User interrupted.");
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    /**
     * This checks the validity of the input card's user code by checking the first
     * character of the code
     */
    private void processUserCode() {
        String code = this.cardReader.getCardCode();
        if (code != null) {
            if (code.charAt(0) != 'A' && code.charAt(0) != 'V') {
                this.userType = 'O';
                this.cardReader.ejectCard();
            } else {
                this.userType = code.charAt(0);
            }
        } else {
            this.userType = 'O';
            this.cardReader.ejectCard();
        }
    }
}
