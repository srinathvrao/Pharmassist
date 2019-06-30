# Pharmassist

## Problem Statement :

Confusing the dosage, frequency and timing of a lot of prescription drugs, can lead to severe problems, including drastically increasing the death rates. Current solutions are too costly, bulky or are extremely hard and tiring to use. 

According to a study done by the World Health Organisation, there are more than 285 million visually impaired people around the world. The majority of people with vision impairment are over the age of 50 years. While the idea was motivated by concern for blind and visually impaired patients, it can be applied to the common old man and in general any patient. 

According to a study performed at University of College London, a third of the elderly population (age 65 and older) is in danger of premature death due to misunderstanding medicine labels.

A study conducted by the Northwestern University back in 2006, showed that most misinterpretations were caused because of numbers being present in the instructions, some didn’t even get to that stage and just took the wrong pills. This is a serious concern and much worse for illiterate people and visually impaired people. 

## Solution : 

Our app aims to tackle this problem using prescriptive labels, stuck to the bottom of the medicine bottle. We are using conductive material such as conductive copper tape to create a circuit between the user and the touch screen to activate the touch screen. The catch however is that each medicine prescribed to the user has a distinct pattern and thus all the user has to do would be to place the bottle in question on top of the screen and he/she will be told which medicine it is and number of pills to be taken, as well as if he/she has already consumed the dosage for that specified time preventing double dosages. 

We have also integrated this app with Alexa so that patients can ask Alexa to tell them about their dosage in the comfort of their beds. In case they need to take medicines, they can use the app to tell them the exact dosage per medicine, based on the time. The app also has a feature to alert the patient to refill his medicine by tracking the number of pills he has taken. To further ease access, the patient can then find pharmacies near him. In future updates we would like to add a delivery service so that the patient can place the order for the medicine on the app itself (after confirming with the doctor). 

In the situation of an Emergency, the app can be used to immediately call the doctor. On this video call, the doctor gets information about the patient, such as his Heart Beat and Blood Pressure. All of this is done by just analysing the patients face, an application explored in this paper : https://people.csail.mit.edu/mrub/vidmag/papers/Balakrishnan_Detecting_Pulse_from_2013_CVPR_paper.pdf

The doctor can then diagnose the patient and can use these metrics in his diagnosis. In the future the  app can also be used to alert loved ones/ concerned authorities if a patient misses his medication or is in any trouble, even after the video call. 

## Scalability : 

We understand that it would be next to impossible to generate a label for each medicine that is present in the world and hence we are following a method where there are a set of labels for each patient as decided by the patients doctor. Thus a label is relative to each patient and say, label 1 for patient X is different than label 1 for patient Y. This way each medicine can be mapped to a label for a specific patient and their accounts (which they use for logging in on their phone) keeps track of which label indicates what drug. Moreover if there is a change in the patient's prescription, the same label can be reprogrammed by the doctor to now represent the new drug prescribed to the patient. 


## Marketability and Business Plan: 

We estimate to spend Rs 0.5 on the label, using conductive tape.
Selling Price : Rs 10 per label. 
This gives us 20x profit. 
 
In order to ensure a recurring revenue stream, we can also add premium accounts to the app itself, charging around Rs. 200 per month, with extra features like alerting concerned parties about missing medications, updating you about the pill count, scheduling doctors appointments for refilling, reminders for refilling, etc. 

We plan on selling these labels to doctors and other pharmacists to attach to the bottom of medicine bottles while selling them. Here we follow a two fold incentive program : 

In the short term, doctors and pharmacists selling the labels can be incentivised to sell the labels. 

In the long term, the patient automatically has to come back to the same doctor as only he will be able to refill his medication and this ensures returning patients, something that automatically incentivises doctors. 

To reduce the cost of making labels, we can explore other materials for the labels as well, such as conductive ink.

