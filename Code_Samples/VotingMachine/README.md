# VotingMachine

## To Run VotingMachine simulation

### Step 1: Running The Machine and Simulation code

Run the Main.java file which instantiates the VoteManager and its subprocesses, the Devices and their Drivers, and 
DeviceSimulatorCLI which is used to tamper/fail devices and physically insert SD Cards, Printer Paper, and User Cards.

### Step 2: Inserting SD Cards and Printer Paper

The Three SD Cards and Printer paper must be physically placed before any further processing occurs

```
p bluesd
p redsd1
p redsd2
p paper
```

### Step 3: Inserting admin card to lock latches and start election

Then an admin card must be entered upon machine initialization (any other card will get rejected)

```
i admin
```

Then the admin screen will appear and the admin will lock the latches, and start election process

### Step 4.1: General Purpose Use

Then the machine will accept admin or voter cards (other cards will get rejected), but only accept one card at a time,
 there is no timeout implemented as of now so current voter or admin must finish there process

When a voter card is inserted, the machine will send a ballot template to the screen for the voter to fill, which is 
 then returned upon confirmation and saved to both red sd cards and printed.

#### Inserting Voter Card

```
i voter
```

#### Inserting Admin Card

```
i admin
```

#### Inserting Other Card

```
i other
```

To Be Continued

### Step 4.2: Failures and Tampers (Add Screen Failure/Tamper)

#### Failures

```
f printer
f latch
f cardreader             
f bluedriver
f reddriver1
f reddriver2
```

#### Tampers

```
t printer
t latch
t cardreader
t bluedriver
t reddriver1
t reddriver2
```

## Voting Machine Hierarchy

### Main.java

Initializes VoteManager, devices, and device simulator

### VoteManager.java

Primary logic controller. Controls and uses DeviceMonitor, User, Admin, and Voter. Handles main logic such as
 waiting for insertion of SD Cards, and Paper, and then processing user codes from User into either Voter or Admin.

### DeviceMonitor.java

Monitors the devices or failures and tampering at all times, and will shut down/close files when devices are 
failed/tampered. Also makes sure to verify SD Card insertions, Paper insertion before running the rest of the devices.

### User.java

Handles the CardReader by scanning and holding a single user card at a time and verifying user code
starts with 'A' or 'V', all other cards are ejected.

### Admin.java (NOT DONE)

Handles the Latch(es) and the Screen, by being able to start and stop elections, and lock/unlock 
latches.

### Voter.java (NOT DONE)

Handles the Screen, SD Cards, and Printer, by displaying Ballot and saving submitted ballots to the SD Cards and
printing them.
