// Jared Baker (NID: ja907583)
// COP 3503, Fall 2022

// Programming Assignment Four: Skip List
import java.io.*;
import java.util.*;

class Node <AnyType extends Comparable<AnyType>>
{
  // data of the node
  AnyType data;
  // height of the node
  int height;
  // an ArrayList of pointers to the next node
  ArrayList <Node<AnyType>> pointers;

  // Creates new node with a specific height
  Node(int height)
  {
    this.data = null;
    this.height = height;
    this.pointers = new ArrayList<>();
    // fills the node pointers all to null
    for (int i = 0; i < height; i ++)
    {
      this.pointers.add(null);
    }
  }

  // Creates a new node with a specific height and initilize the data
  Node(AnyType data, int height)
  {
    this.data = data;
    this.height = height;
    this.pointers = new ArrayList<>();
    // fill the node pointers all to null
    for (int i = 0; i < height; i ++)
    {
      this.pointers.add(null);
    }
  }

  // Returns the value of the Node
  public AnyType value()
  {
    return this.data;
  }

  // Returns the height of the Node
  public int height()
  {
    return this.height;
  }

  // returns the next node in the skip list at a the level passed
  public Node<AnyType> next(int level)
  {
    if (level < 0 || level > (this.height - 1))
    {
      return null;
    }
    return this.pointers.get(level);
  }

  // Suggested Methods
  // Set the next refrence at the given level
  public void setNext(int level, Node<AnyType> node)
  {
    this.pointers.set(level, node);
  }

  // Grow the node by exactly one
  public void grow()
  {
    this.pointers.add(null);
    this.height += 1;
  }

  // Grow this node by exactly one with a 50% chance
  public int maybeGrow()
  {
    // picks a random number between 0 and 1
    Random chance = new Random();
    int odds = chance.nextInt(2);

    // If the odds are 1 then it will grow else it wont
    if (odds == 1)
    {
      this.pointers.add(null);
      this.height += 1;
    }
    return odds;
  }

  // remove refrences from the top node of the tower
  public void trim(int height)
  {
    while (this.height > height)
    {
      this.pointers.remove(this.height - 1);
      this.height -= 1;
    }
  }
}

public class SkipList <AnyType extends Comparable<AnyType>>
{
  // Initilize what is needed to start the SkipList
  Node<AnyType> headNode;
  int height;
  int size;

  // creates a new SkipList with head node start with size one
  SkipList()
  {
    headNode = new Node<AnyType>(1);
    this.height = 1;
    this.size = 0;
  }

  // Creates a new SkipList with a head node at a given height greter than 0
  SkipList(int height)
  {
    if (height < 1)
    {
      height = 1;
    }
    headNode = new Node<AnyType>(height);
    this.height = height;
    this.size = 0;
  }

  // Return the number of nodes in the SkipList
  public int size()
  {
    return this.size;
  }

  // Return the height of the SkipList head node
  public int height()
  {
    return head().height;
  }

  // Return the head of the SkipList
  public Node<AnyType> head()
  {
    return this.headNode;
  }

  // Insert data into the SkipList with a random height
  public void insert(AnyType data)
  {
    // initilize helpers to traverse the skiplist to find where to add the new node
    Node<AnyType> currentNode = head();
    int nodeLevel = this.height() - 1;
    // gets a random height at the height of the head node
    int randomHeight = generateRandomHeight(this.height());
    // creats a node with the data and the random height
    Node<AnyType> tempNode = new Node<AnyType>(data, randomHeight);

    // loops through the skip list
    // once the skiplist nodeLevel is below 0 then we have added the node to the skiplist
    while (nodeLevel >= 0)
    {
      // if the next node is empty or the next node data is to large
      if (currentNode.next(nodeLevel) == null ||
          currentNode.next(nodeLevel).value().compareTo(data) >= 0)
      {
        // checks if the new node is too short to be added if we found a spot
        if (nodeLevel < randomHeight)
        {
          tempNode.setNext(nodeLevel,currentNode.next(nodeLevel));
          currentNode.setNext(nodeLevel,tempNode);
        }
        nodeLevel --;
      }
      // if th next node data is smaller than the data we want to input
      else
      {
        currentNode = currentNode.next(nodeLevel);
      }
    }
    // increase size of the skiplist once we have added the node
    this.size += 1;

    // determines if we need to increase the head node of the SkipList
    if (getMaxHeight(this.size) > head().height())
    {
        this.growSkipList();
    }
  }

  // Insert data into the SkipList with a set height
  public void insert(AnyType data, int height)
  {
    // initilize helpers to traverse the skiplist to find where to add the new node
    Node<AnyType> currentNode = head();
    int nodeLevel = this.height() - 1;
    // creats a node with the data and the height
    Node<AnyType> tempNode = new Node<AnyType>(data,height);

    // loops through the skip list
    // once the skiplist nodeLevel is below 0 then we have added the node to the skiplist
    while (nodeLevel >= 0)
    {
      // if the next node is empty or the next node data is to large
      if (currentNode.next(nodeLevel) == null ||
          currentNode.next(nodeLevel).value().compareTo(data) >= 0)
      {
        // checks if the new node is too short to be added if we found a spot
        if (nodeLevel < height)
        {
          tempNode.setNext(nodeLevel,currentNode.next(nodeLevel));
          currentNode.setNext(nodeLevel,tempNode);
        }
        nodeLevel --;
      }
      // if th next node data is smaller than the data we want to input
      else
      {
        currentNode = currentNode.next(nodeLevel);
      }
    }
    // increase size of the skiplist once we have added the node
    this.size += 1;

    // determines if we need to increase the head node of the SkipList
    if (getMaxHeight(this.size) > this.height())
    {
        this.growSkipList();
    }
  }

  // Delete a node from the SkipList
  public void delete(AnyType data)
  {
    // initilize helpers to traverse the skiplist to find the node we want to delete
    Node<AnyType> currentNode = head();
    int nodeLevel = this.height() - 1;
    // an arraylist of the nodes that are pointing to the node with data equal to the data
    // we want to delete
    ArrayList<Node<AnyType>> savedPointers = new ArrayList<>();

    // initilize the savedPointers to null to use .set()
    for (int i = 0; i < this.height(); i++)
    {
      savedPointers.add(null);
    }

    // loop through the skiplist
    while (nodeLevel >= 0)
    {
      // checks to see if the next node at the level is equal to the data we want to delete
      if (currentNode.next(nodeLevel) == null ||
          currentNode.next(nodeLevel).value().compareTo(data) >= 0)
      {
        if (currentNode.next(nodeLevel) == null)
        {
          // does nothing if the pointers that are going to be replaced are not pointing to
          // another node in the skiplist
        }
        // If we find the data to remove we saved what that node is pointing to at that level
        else if (currentNode.next(nodeLevel).value().compareTo(data) == 0)
        {
          // saves the node that is pointing to the node we want to delete
          savedPointers.set(nodeLevel, currentNode);
        }
          nodeLevel --;
      }
      // if the data of the next node is less than the data of the node we want to delete
      else if (currentNode.next(nodeLevel).value().compareTo(data) < 0)
      {
        currentNode = currentNode.next(nodeLevel);
      }
    }

    // no pointers were saved to get moved when the node gets deleted
    if (savedPointers.get(0) == null)
    {
      return;
    }

    // gets the height of the node we want to delete
    int heightDelete = savedPointers.get(0).next(0).height;

    // loops through the saved nodes changin the next node to what the delete nodes was pointing
    // at so it gets removed from the list
    for (int j = 0; j < heightDelete; j++)
    {
      if (savedPointers.get(j) == null)
      {
        break;
      }
      savedPointers.get(j).setNext(j,savedPointers.get(j).next(j).next(j));
    }
    //decreases the number of nodes in the skiplist after removing the node
    this.size --;

    // loops through trim until the height is log2(n)
    while (getMaxHeight(this.size) < this.height())
    {
        this.trimSkipList();
    }
  }

  // Return true if the SkipList contains data
  public boolean contains(AnyType data)
  {
    // initilize helpers to travers the skiplist
    int nodeLevel = head().height - 1;
    Node<AnyType> currentNode = head();

    while (nodeLevel >= 0)
    {
      // goes down a level in the skiplist if the data is less than the node it is pointing to
      if (currentNode.next(nodeLevel) == null ||
          currentNode.next(nodeLevel).value().compareTo(data) > 0)
      {
        nodeLevel --;
      }
      // traverses the SkipList if the data is greater than the node it is pointing to
      else if (currentNode.next(nodeLevel).value().compareTo(data) < 0)
      {
        currentNode = currentNode.next(nodeLevel);
      }
      // if the node it is pointing at is equal to the data that we are wanting to find
      else
      {
        return true;
      }
    }
    // if it traverses through the skiplist and doesnt find a matching value
    return false;
  }

  // Return a node in the SkipList that contains data similar to contains() method
  public Node<AnyType> get(AnyType data)
  {
    // helpers to travers the skiplist
    Node<AnyType> currentNode = head();
    int nodeLevel = head().height - 1;

    while (nodeLevel >= 0)
    {
      // goes down a level in the skiplist if the data is less than the node it is pointing to
      if (currentNode.next(nodeLevel) == null ||
          currentNode.next(nodeLevel).value().compareTo(data) > 0)
      {
        nodeLevel --;
      }
      // traverses the SkipList if the data is greater than the node it is pointing to
      else if (currentNode.next(nodeLevel).value().compareTo(data) < 0)
      {
        currentNode = currentNode.next(nodeLevel);
      }
      // if the node it is pointing at is equal to the data that we are wanting to find
      else
      {
        return currentNode.next(nodeLevel);
      }
    }
    // if it traverses through the skiplist and doesnt find a matching value
    return null;
  }

  // Suggested Methods
  // Returns max height
  private static int getMaxHeight(int n)
  {
    int base = 2;
    int exp = 1;

    while (n > base)
    {
      base *= 2;
      exp ++;
    }
    return exp;
  }

  // Returns a random height
  private static int generateRandomHeight(int maxHeight)
  {
    Random chance = new Random();
    int randomHeight = 1;
    int odds;
    while (randomHeight < maxHeight)
    {
      odds = chance.nextInt(2);
      if (odds == 0)
      {
        return randomHeight;
      }
      randomHeight ++;
    }
    return randomHeight;
  }

  // Grow SkipList for the insert method
  private void growSkipList()
  {
    // helps travers the node of the skiplist
    Node<AnyType> currentNode = head();
    Node<AnyType> previousNode = head();
    // saves the original height of the skiplist to travers nodes only at this level
    int originalLevel = head().height - 1;

    // grow the head node
    head().grow();

    // loop through the skiplist of the nodes that were originally max height
    // and see if they increase to the new height
    while (currentNode.next(originalLevel) != null)
    {
      currentNode = currentNode.next(originalLevel);
      // sees if the current node grows
      if (currentNode.maybeGrow() == 1)
      {
        previousNode.setNext(height() - 1, currentNode);
        previousNode = currentNode;
      }
    }
  }

  // Trim the SkipList for delete method
  private void trimSkipList()
  {
    // initilize helpers to travers the node
    Node<AnyType> currentNode = head();
    int originalLevel = head().height - 1;
    // If the top of the head node doesnt point to any node that needs to be trimmed
    while (head().next(originalLevel) != null)
    {
      // travers through the skiplist by looking at the next node
      currentNode = currentNode.next(originalLevel);
      if (currentNode == null)
      {
        break;
      }
      // if the node at the originalLevel is pointing to null trim the height
      else if (currentNode.next(originalLevel) == null)
      {
        currentNode.trim(originalLevel);
      }
      // have the head node point to the node that the current nodes originalLevel was point at
      else
      {
        head().setNext(originalLevel,currentNode.next(originalLevel));
        currentNode.trim(originalLevel);
        currentNode = head();
      }
    }
    // once all the other nodes are lowered then the nead node is lowered
    head().trim(originalLevel);
  }

  // Difficulty rating
  public static double difficultyRating()
  {
    return 5.0;
  }

  // Hours spent
  public static double hoursSpent()
  {
    return 30.0;
  }
}
