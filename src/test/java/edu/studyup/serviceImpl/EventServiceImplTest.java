package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEventName_WrongEventID_BadCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	@Test
	void testUpdateEventName_TooLong_BadCase() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "This event name is too long.");
		});
	}
	
	@Test
	void testUpdateEventName_AtMaxLength_Case() throws StudyUpException {
		int eventID = 1;
		String eventName = "This is 20 chars!!!!";
		eventServiceImpl.updateEventName(eventID, "This is 20 chars!!!!");
		assertEquals("This is 20 chars!!!!", DataStorage.eventData.get(1).getName());
	}
	
	@Test
	void testUpdateEventName_SameName_BadCase() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Event 1");
		});
	}
	
	@Test
	void testAddStudent_TwoStudents_GoodCase() throws StudyUpException {
		Event event = new Event();
		event.setName("Add Student Event");
		event.setEventID(1);
		
		Student student = new Student();
		student.setFirstName("Joe");
		student.setLastName("Barry");
		student.setEmail("joe@gmail.com");
		student.setId(2);
		
		eventServiceImpl.addStudentToEvent(student, event.getEventID());
		assertEquals(true, DataStorage.eventData.get(1).getStudents().contains(student));
	}
	
	@Test
	void testAddStudent_ThreeStudents_GoodCase() throws StudyUpException {
		Event event = new Event();
		event.setName("Add Student Event");
		event.setEventID(1);
		
		Student student1 = new Student();
		student1.setFirstName("Joe");
		student1.setLastName("Barry");
		student1.setEmail("joe@gmail.com");
		student1.setId(2);
		
		Student student2 = new Student();
		student2.setFirstName("Jill");
		student2.setLastName("Johnson");
		student2.setEmail("jill@gmail.com");
		student2.setId(3);
		
		eventServiceImpl.addStudentToEvent(student1, event.getEventID());
		System.out.print(DataStorage.eventData.get(1).getStudents().size());
		
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student2, event.getEventID());
		});
	}
	
	@Test
	void testAddStudent_FirstStudent_GoodCase() throws StudyUpException {
		int eventID = 1;
		DataStorage.eventData.get(1).getStudents().clear();
		
		Student student = new Student();
		student.setFirstName("Joe");
		student.setLastName("Barry");
		student.setEmail("joe@gmail.com");
		student.setId(1);
		
		eventServiceImpl.addStudentToEvent(student, eventID);
		assertEquals(true, DataStorage.eventData.get(1).getStudents().contains(student));
	}
	
//	@Test
//	void testAddStudent_DuplicateStudent_BadCase() {
//		Event event = new Event();
//		event.setName("Add Duplicate Student Event");
//		event.setEventID(1);
//		
//		Student student = new Student();
//		student.setFirstName("John");
//		student.setLastName("Doe");
//		student.setEmail("JohnDoe@email.com");
//		student.setId(1);
//		
//		Assertions.assertThrows(StudyUpException.class, () -> {
//			eventServiceImpl.addStudentToEvent(student, event.getEventID());
//		});	
//	}
	
	@Test
	void testAddStudent_WrongEventID_BadCase() {
		Student student = new Student();
		student.setFirstName("Joe");
		student.setLastName("Barry");
		student.setEmail("joe@gmail.com");
		student.setId(2);
		
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student, 4);
		});
	}
	
	@Test
	void testAddStudent_NoPresentStudents() throws StudyUpException {
		List<Student> students = null;
		DataStorage.eventData.get(1).setStudents(students);
		
		Student newStudent = new Student();
		newStudent.setFirstName("Joe");
		newStudent.setLastName("Barry");
		newStudent.setEmail("joe@gmail.com");
		newStudent.setId(1);
		
		eventServiceImpl.addStudentToEvent(newStudent, 1);
		assertEquals(true, DataStorage.eventData.get(1).getStudents().contains(newStudent));
	}
	
	@Test
	void testGetActiveEvents_WithEvent_GoodCase() {
		DataStorage.eventData.clear();

		Date futureDate = new Date(124, 1, 1);
		
		Event event = new Event();
		event.setName("Future event");
		event.setEventID(1);
		event.setDate(futureDate);
		
		DataStorage.eventData.put(1, event);
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		assertEquals(true, activeEvents.contains(event));
	}
	
	@Test
	void testGetActiveEvents_NoEvent_BadCase() {
		DataStorage.eventData.clear();

		Date pastDate = new Date(97, 1, 1);

		Event event = new Event();
		event.setName("Past event");
		event.setEventID(1);
		event.setDate(pastDate);
		
		DataStorage.eventData.put(1, event);
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		assertEquals(false, activeEvents.contains(event));
	}
	
	@Test
	void testGetPastEvents_WithEvent_GoodCase() {
		DataStorage.eventData.clear();
		
		Date date = new Date(97, 1, 1);
		
		Event event = new Event();
		event.setName("Past event");
		event.setEventID(1);
		event.setDate(date);
		
		DataStorage.eventData.put(1, event);
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		assertEquals(false, pastEvents.isEmpty());
	}
	
	@Test
	void testGetPastEvents_NoEvent_BadCase() {
		DataStorage.eventData.clear();
		
		Date futureDate = new Date(124, 1, 1);
		
		Event event = new Event();
		event.setName("Future event");
		event.setEventID(1);
		event.setDate(futureDate);
		
		DataStorage.eventData.put(1, event);
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		assertEquals(true, pastEvents.isEmpty());
	}
	
	@Test
	void testDeleteEvent_GoodCase() {
		eventServiceImpl.deleteEvent(1);
		assertEquals(false, DataStorage.eventData.containsKey(1));
	}

}
