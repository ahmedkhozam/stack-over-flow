<h1 align="center">Design Stack Overflow</h1>
<h3 align="center">Let's design an online community for developers.</h3>

**We'll cover the following:**

* [System Requirements](#system-requirements)
* [Use Case Diagram](#use-case-diagram)
* [Class Diagram](#class-diagram)
* [Activity Diagram](#activity-diagram)
* [Sequence Diagram](#sequence-diagram)
* [Code](#code)

Stack Overflow is one of the largest online communities for developers to learn and share their knowledge. The website provides a platform for its users to ask and answer questions, and through membership and active participation, to vote questions and answers up or down. Users can edit questions and answers in a fashion similar to a [wiki](https://en.wikipedia.org/wiki/Wiki).

Users of Stack Overflow can earn reputation points and badges. For example, a person is awarded ten reputation points for receiving an “up” vote on an answer and five points for the “up” vote of a question. The can also receive badges for their valued contributions. A higher reputation lets users unlock new privileges like the ability to vote, comment on, and even edit other people’s posts.

![Image](https://github.com/user-attachments/assets/7e71da4d-601d-40f8-8a46-23c820effb2f)
### System Requirements

We will be designing a system with the following requirements:

1. Any non-member (guest) can search and view questions. However, to add or upvote a question, they have to become a member.
2. Members should be able to post new questions.
3. Members should be able to add an answer to an open question.
4. Members can add comments to any question or answer.
5. A member can upvote a question, answer or comment.
6. Members can flag a question, answer or comment, for serious problems or moderator attention.
7. Any member can add a [bounty](https://stackoverflow.com/help/bounty) to their question to draw attention.
8. Members will earn [badges](https://stackoverflow.com/help/badges) for being helpful.
9. Members can vote to [close](https://stackoverflow.com/help/closed-questions) a question; Moderators can close or reopen any question.
10. Members can add [tags](https://stackoverflow.com/help/tagging) to their questions. A tag is a word or phrase that describes the topic of the question.
11. Members can vote to [delete](https://stackoverflow.com/help/deleted-questions) extremely off-topic or very low-quality questions.
12. Moderators can close a question or undelete an already deleted question.
13. The system should also be able to identify most frequently used tags in the questions.

### Use Case Diagram

We have five main actors in our system:

* **Admin:** Mainly responsible for blocking or unblocking members.
* **Guest:** All guests can search and view questions.
* **Member:** Members can perform all activities that guests can, in addition to which they can add/remove questions, answers, and comments. Members can delete and un-delete their questions, answers or comments.
* **Moderator:** In addition to all the activities that members can perform, moderators can close/delete/undelete any question.
* **System:** Mainly responsible for sending notifications and assigning badges to members.

Here are the top use cases for Stack Overflow:

1. Search questions.
2. Create a new question with bounty and tags.
3. Add/modify answers to questions.
4. Add comments to questions or answers.
5. Moderators can close, delete, and un-delete any question.

Here is the use case diagram of Stack Overflow:

![Image](https://github.com/user-attachments/assets/f1718bc3-e6e6-44bc-a0a5-beb7ba61ffaf)
    Use Case Diagram for Stack Overflow

### Class Diagram

Here are the main classes of Stack Overflow System:

* **Question:** This class is the central part of our system. It has attributes like Title and Description to define the question. In addition to this, we will track the number of times a question has been viewed or voted on. We should also track the status of a question, as well as closing remarks if the question is closed.
* **Answer:** The most important attributes of any answer will be the text and the view count. In addition to that, we will also track the number of times an answer is voted on or flagged. We should also track if the question owner has accepted an answer.
* **Comment:** Similar to answer, comments will have text, and view, vote, and flag counts. Members can add comments to questions and answers.
* **Tag:** Tags will be identified by their names and will have a field for a description to define them. We will also track daily and weekly frequencies at which tags are associated with questions.
* **Badge:** Similar to tags, badges will have a name and description.
* **Photo:** Questions or answers can have photos.
* **Bounty:** Each member, while asking a question, can place a bounty to draw attention. Bounties will have a total reputation and an expiry date.
* **Account:** We will have four types of accounts in the system, guest, member, admin, and moderator. Guests can search and view questions. Members can ask questions and earn reputation by answering questions and from bounties.
* **Notification:** This class will be responsible for sending notifications to members and assigning badges to members based on their reputations.

![Image](https://github.com/user-attachments/assets/f366206b-72f7-4de5-9e05-fb2fce8f8285)

![Image](https://github.com/user-attachments/assets/1b7a03a6-bed4-4bf8-98d6-9445b3d9a100)

### Activity Diagram

**Post a new question:** Any member or moderator can perform this activity. Here are the steps to post a question:

![Image](https://github.com/user-attachments/assets/e06a3c2b-cf89-45d9-b7ca-e4a6c3184b4a)

### Sequence Diagram

Following is the sequence diagram for creating a new question:

![Image](https://github.com/user-attachments/assets/e7614a92-4ca1-4c83-94db-3f8ec8e4fe59)
