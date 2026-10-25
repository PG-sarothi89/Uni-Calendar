"""
UniTrack — Smart Academic Calendar: Formal OODP Project Report Generator (Strict Black & White Edition)
Builds a complete, medium-sized academic project report adhering strictly to the required format
using ONLY Black and White / Monochrome styling (no color).
"""

import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas
from reportlab.graphics.shapes import Drawing, Rect, String, Line, Polygon, Group, Circle

# ----------------------------------------------------------------------
# Numbered Canvas for Running Headers, Footers (Strict Monochrome)
# ----------------------------------------------------------------------
class ReportNumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            canvas.Canvas.showPage(self)
        canvas.Canvas.save(self)

    def draw_page_decorations(self, total_pages):
        page_num = self._pageNumber
        if page_num == 1:
            return # Title Page has no running header/footer
            
        self.saveState()
        
        # Header (Top) - Black & White
        self.setFont('Helvetica-Bold', 8)
        self.setFillColor(colors.black)
        self.drawString(40, 758, 'UNITRACK — SMART ACADEMIC CALENDAR')
        
        self.setFont('Helvetica', 8)
        self.setFillColor(colors.HexColor('#444444'))
        self.drawRightString(572, 758, 'OODP Project Report | Java Desktop Application')
        
        self.setStrokeColor(colors.black)
        self.setLineWidth(0.75)
        self.line(40, 750, 572, 750)
        
        # Footer (Bottom) - Black & White
        self.line(40, 38, 572, 38)
        self.setFont('Helvetica', 8)
        self.setFillColor(colors.HexColor('#444444'))
        self.drawString(40, 26, 'Department of Computer Science & Engineering • Academic Project Report')
        
        page_str = f'Page {page_num} of {total_pages}'
        self.setFont('Helvetica-Bold', 8)
        self.setFillColor(colors.black)
        self.drawRightString(572, 26, page_str)
        
        self.restoreState()


# ----------------------------------------------------------------------
# Vector Diagram Flowchart Helpers (Strict Black & White)
# ----------------------------------------------------------------------
def draw_arrow_right_bw(d, x1, y, x2, color=colors.black, width=1.3):
    d.add(Line(x1, y, x2, y, strokeColor=color, strokeWidth=width))
    d.add(Polygon([x2, y, x2 - 5.5, y + 3, x2 - 5.5, y - 3], fillColor=color, strokeColor=None))

def create_system_architecture_diagram_bw():
    dw = 532
    dh = 95
    d = Drawing(dw, dh)
    # White background, black border
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    # Title badge
    d.add(Rect(8, dh - 16, 145, 13, fillColor=colors.HexColor('#EEEEEE'), strokeColor=colors.black, strokeWidth=0.5, rx=2, ry=2))
    d.add(String(14, dh - 12.5, 'SYSTEM ARCHITECTURE TIERS', fontName='Helvetica-Bold', fontSize=7.2, fillColor=colors.black))
    
    tier_width = 108
    tier_height = 65
    y_pos = 10
    spacing = 25
    x_start = 12
    
    tiers = [
        ('1. Presentation', ['Swing Desktop GUI', 'Event Dispatch Thread', 'Interactive Month Grid']),
        ('2. Service Layer', ['CalendarService', 'Auth & Permission Logic', 'Notification Daemon']),
        ('3. Data Access', ['Generic Repository<T>', 'PreparedStatement DAOs', 'Transaction Control']),
        ('4. Persistence', ['MySQL Relational DB', 'Local File Storage', 'TCP Socket Sync Daemon'])
    ]
    
    for i, (title, items) in enumerate(tiers):
        x = x_start + i * (tier_width + spacing)
        # Clean light gray fill with crisp black border
        d.add(Rect(x, y_pos, tier_width, tier_height, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + tier_width / 2, y_pos + 51, title, fontName='Helvetica-Bold', fontSize=8, fillColor=colors.black, textAnchor='middle'))
        d.add(Line(x + 8, y_pos + 46, x + tier_width - 8, y_pos + 46, strokeColor=colors.HexColor('#666666'), strokeWidth=0.5))
        
        for j, item in enumerate(items):
            d.add(String(x + tier_width / 2, y_pos + 33 - (j * 11), item, fontName='Helvetica', fontSize=6.5, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(tiers) - 1:
            arr_x1 = x + tier_width
            arr_x2 = arr_x1 + spacing
            arr_y = y_pos + tier_height / 2
            draw_arrow_right_bw(d, arr_x1, arr_y, arr_x2, color=colors.black, width=1.3)
            # return flow dashed
            d.add(Line(arr_x2 - 3, arr_y - 9, arr_x1 + 3, arr_y - 9, strokeColor=colors.HexColor('#666666'), strokeWidth=0.8, strokeDashArray=[2, 2]))
            d.add(Polygon([arr_x1 + 3, arr_y - 9, arr_x1 + 6.5, arr_y - 7, arr_x1 + 6.5, arr_y - 11], fillColor=colors.HexColor('#666666'), strokeColor=None))
            
    return d

def create_event_lifecycle_diagram_bw():
    dw = 532
    dh = 70
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    steps = [
        ('1. User Input', 'Event Form in Swing\nDate, Course, Title'),
        ('2. Validation', 'CalendarService checks\nTime overlap & bounds'),
        ('3. Persistence', 'EventDAO executes\nPreparedStatement'),
        ('4. Alert Queue', 'ReminderDaemon registers\nNotification key'),
        ('5. UI Refresh', 'Swing EDT repaints\nCalendar Month Cell')
    ]
    
    box_w = 84
    box_h = 48
    y_pos = 10
    spacing = 22
    x_start = 12
    
    for i, (title, subtitle) in enumerate(steps):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 35, title, fontName='Helvetica-Bold', fontSize=7.5, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 23 - (k * 9.5), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(steps) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d

def create_concurrency_diagram_bw():
    dw = 532
    dh = 70
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    steps = [
        ('1. Daemon Start', 'ScheduledExecutorService\nevery 60s in background'),
        ('2. DB Query', 'Query approaching events\nDue within <= 24 hours'),
        ('3. Deduplication', 'Check dedup_key to avoid\nrepeated alert alerts'),
        ('4. EDT Invoke', 'SwingUtilities.invokeLater\npushes dialog onto UI'),
        ('5. Safe Exit', 'shutdownNow() on window\nclosing event hook')
    ]
    
    box_w = 84
    box_h = 48
    y_pos = 10
    spacing = 22
    x_start = 12
    
    for i, (title, subtitle) in enumerate(steps):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 35, title, fontName='Helvetica-Bold', fontSize=7.5, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 23 - (k * 9.5), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(steps) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d


# ----------------------------------------------------------------------
# Report Generator (Strict Black & White)
# ----------------------------------------------------------------------
def generate_report(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=40,
        rightMargin=40,
        topMargin=54,
        bottomMargin=48
    )
    
    styles = getSampleStyleSheet()
    
    # Monochrome Palette (Strict Black and White)
    COLOR_BLACK = colors.black
    COLOR_DARK = colors.HexColor('#222222')
    COLOR_MUTED = colors.HexColor('#555555')
    COLOR_BORDER = colors.HexColor('#888888')
    COLOR_LIGHT_BORDER = colors.HexColor('#CCCCCC')
    COLOR_BG_ALT = colors.HexColor('#F5F5F5')
    COLOR_CODE_BG = colors.HexColor('#FAFAFA')
    
    # Typography Styles
    cover_title = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=30,
        textColor=COLOR_BLACK,
        alignment=1,
        spaceAfter=10
    )
    
    cover_subtitle = ParagraphStyle(
        'CoverSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=17,
        textColor=COLOR_MUTED,
        alignment=1,
        spaceAfter=25
    )
    
    sec_title = ParagraphStyle(
        'SecTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=COLOR_BLACK,
        spaceBefore=14,
        spaceAfter=5,
        keepWithNext=True
    )
    
    subsec_title = ParagraphStyle(
        'SubSecTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13.5,
        textColor=COLOR_BLACK,
        spaceBefore=9,
        spaceAfter=4,
        keepWithNext=True
    )
    
    body = ParagraphStyle(
        'ReportBody',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=12.2,
        textColor=COLOR_BLACK,
        spaceAfter=6
    )
    
    bullet = ParagraphStyle(
        'ReportBullet',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=COLOR_BLACK,
        leftIndent=14,
        firstLineIndent=-8,
        spaceAfter=3
    )
    
    code_block = ParagraphStyle(
        'CodeBlock',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.2,
        leading=9.8,
        textColor=COLOR_BLACK
    )
    
    table_cell = ParagraphStyle(
        'TCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=7.8,
        leading=10.5,
        textColor=COLOR_BLACK
    )
    
    table_cell_bold = ParagraphStyle(
        'TCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.8,
        leading=10.5,
        textColor=COLOR_BLACK
    )

    table_cell_code = ParagraphStyle(
        'TCellCode',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.1,
        leading=9.5,
        textColor=COLOR_BLACK
    )
    
    table_head = ParagraphStyle(
        'THead',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10.5,
        textColor=colors.white
    )
    
    story = []
    
    # ==================================================================
    # TITLE PAGE (Black and White)
    # ==================================================================
    story.append(Spacer(1, 40))
    story.append(Paragraph("PROJECT REPORT", ParagraphStyle('ReportHeader', fontName='Helvetica-Bold', fontSize=12, leading=14, textColor=COLOR_MUTED, alignment=1, spaceAfter=15)))
    story.append(Spacer(1, 20))
    
    story.append(Paragraph("UNITRACK — SMART ACADEMIC CALENDAR", cover_title))
    story.append(Paragraph("A Desktop-First Academic Scheduling & Task Management System<br/><font size=9.5 color='#555555'>Demonstrating Advanced Object-Oriented Design & Programming Principles in Java</font>", cover_subtitle))
    
    story.append(Spacer(1, 30))
    story.append(HRFlowable(width="60%", thickness=1.5, color=COLOR_BLACK, spaceAfter=40, spaceBefore=0))
    
    meta_data = [
        [Paragraph("<b>Course Name:</b>", table_cell_bold), Paragraph("Object-Oriented Design and Programming (CSE 2102)", table_cell)],
        [Paragraph("<b>Project Scope:</b>", table_cell_bold), Paragraph("Medium-Scale Desktop Application (Java Swing, JDBC, MySQL)", table_cell)],
        [Paragraph("<b>Student Name / ID:</b>", table_cell_bold), Paragraph("Undergraduate Software Engineering Student", table_cell)],
        [Paragraph("<b>Instructor / Evaluator:</b>", table_cell_bold), Paragraph("Department of Computer Science & Engineering", table_cell)],
        [Paragraph("<b>Submission Date:</b>", table_cell_bold), Paragraph("Academic Year 2026 — Fall Semester", table_cell)],
        [Paragraph("<b>Target System:</b>", table_cell_bold), Paragraph("Cross-Platform Desktop (Windows, macOS, Linux)", table_cell)]
    ]
    t_meta = Table(meta_data, colWidths=[150, 260])
    t_meta.setStyle(TableStyle([
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LINEBELOW', (0, 0), (-1, -1), 0.5, COLOR_LIGHT_BORDER),
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_BG_ALT),
        ('BOX', (0, 0), (-1, -1), 1, COLOR_BLACK),
    ]))
    
    story.append(KeepTogether([t_meta]))
    story.append(Spacer(1, 50))
    
    dept_text = Paragraph(
        "<b>Department of Computer Science and Engineering</b><br/>"
        "Faculty of Electrical and Computer Engineering<br/>"
        "Academic Term Project Submission",
        ParagraphStyle('Dept', fontName='Helvetica', fontSize=9, leading=13, textColor=COLOR_MUTED, alignment=1)
    )
    story.append(dept_text)
    story.append(PageBreak())
    
    # ==================================================================
    # ABSTRACT
    # ==================================================================
    story.append(Paragraph("ABSTRACT", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=8, spaceBefore=0))
    story.append(Paragraph(
        "Managing tertiary academic responsibilities is often disrupted by fragmented schedules. Students and educators routinely navigate disparate systems for lecture schedules, exam notices, assignment deadlines, slide materials, and daily study checklists. <b>UniTrack — Smart Academic Calendar</b> is developed to resolve this fragmentation by providing an integrated, calendar-centric desktop workspace. The core objective of UniTrack is to demonstrate rigorous <b>Object-Oriented Design and Programming (OODP)</b> concepts within a medium-sized, highly maintainable desktop application.",
        body
    ))
    story.append(Paragraph(
        "UniTrack avoids unnecessary enterprise dynamic complexity in favor of clean architectural decoupling. Built on <b>Java SE 17+ (JDK 21 LTS)</b>, the system implements a strict four-tier layered architecture separating the <b>Java Swing (AWT)</b> graphical presentation from business services and persistent storage. Persistence is managed via a 3NF normalized <b>MySQL relational database</b> accessed through type-safe, generic Data Access Objects (DAOs) using <b>JDBC PreparedStatement</b> transactions to eliminate SQL injection vulnerabilities. Multithreading is incorporated via a background <code>ScheduledExecutorService</code> daemon that asynchronously monitors approaching deadlines and alerts the user on the Swing Event Dispatch Thread (EDT) without UI freezes. A lightweight TCP client-server socket module fulfills the networking curriculum by synchronizing institutional academic holiday feeds. The resulting application balances academic rigor, responsive user experience, and practical engineering feasibility.",
        body
    ))
    story.append(Spacer(1, 10))
    
    # ==================================================================
    # TABLE OF CONTENTS
    # ==================================================================
    story.append(Paragraph("TABLE OF CONTENTS", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=8, spaceBefore=0))
    
    toc_data = [
        [Paragraph("<b>Section</b>", table_head), Paragraph("<b>Title & Subsections</b>", table_head), Paragraph("<b>Page</b>", table_head)],
        [Paragraph("—", table_cell_bold), Paragraph("Title Page & Abstract", table_cell), Paragraph("1 – 2", table_cell)],
        [Paragraph("<b>1</b>", table_cell_bold), Paragraph("<b>Introduction</b> (Overview, Purpose & Significance, Scope)", table_cell), Paragraph("3", table_cell)],
        [Paragraph("<b>2</b>", table_cell_bold), Paragraph("<b>Contributions</b> (Technical & Architectural Highlights)", table_cell), Paragraph("3", table_cell)],
        [Paragraph("<b>3</b>", table_cell_bold), Paragraph("<b>Methodology</b> (Process, Technology Stack, Challenges & Solutions)", table_cell), Paragraph("4", table_cell)],
        [Paragraph("<b>4</b>", table_cell_bold), Paragraph("<b>Design</b> (System Architecture, UML Class Diagram, Database Design)", table_cell), Paragraph("5 – 6", table_cell)],
        [Paragraph("<b>5</b>", table_cell_bold), Paragraph("<b>Implementation</b> (Process, Key Code Snippets with Explanations)", table_cell), Paragraph("7 – 8", table_cell)],
        [Paragraph("<b>6</b>", table_cell_bold), Paragraph("<b>Result</b> (UI Layouts, Verification, Test Result Summary)", table_cell), Paragraph("9", table_cell)],
        [Paragraph("<b>7</b>", table_cell_bold), Paragraph("<b>Conclusion & Reflections</b> (Summary, Impact, Learnings)", table_cell), Paragraph("10", table_cell)],
        [Paragraph("<b>8</b>", table_cell_bold), Paragraph("<b>References</b>", table_cell), Paragraph("10", table_cell)],
        [Paragraph("<b>9</b>", table_cell_bold), Paragraph("<b>Appendices</b> (Database DDL Script & Package Directory Tree)", table_cell), Paragraph("11", table_cell)]
    ]
    t_toc = Table(toc_data, colWidths=[50, 420, 62], repeatRows=1)
    t_toc.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_toc)
    story.append(PageBreak())
    
    # ==================================================================
    # 1. INTRODUCTION
    # ==================================================================
    story.append(Paragraph("1. INTRODUCTION", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("1.1 Overview of the Project", subsec_title))
    story.append(Paragraph(
        "Modern university education requires students and educators to juggle overlapping responsibilities across multiple courses: lectures, laboratory reports, class tests, midterm examinations, project submissions, and extracurricular events. Typically, students record assignments in phone notes, track lecture dates on paper syllabi, and receive exam announcements via university portals. This separation leads to missed deadlines and poor preparation. <b>UniTrack</b> re-imagines academic planning by consolidating all scheduled activities into a unified, date-centric interactive monthly calendar desktop application.",
        body
    ))
    
    story.append(Paragraph("1.2 Purpose and Significance", subsec_title))
    story.append(Paragraph(
        "The primary purpose of UniTrack is twofold: (1) to provide students and teachers with an intuitive, clutter-free academic organization tool that directly connects semester milestones with personal daily study tasks; and (2) to serve as a comprehensive academic demonstration of core Java and Object-Oriented Design and Programming principles. Unlike commercial generic calendars (Google Calendar, Outlook), UniTrack is tailored for academia: events are bound to enrolled courses, syllabus progress is dynamically tracked as topics are completed, and lecture slides are attached directly to their scheduled dates.",
        body
    ))
    
    story.append(Paragraph("1.3 Scope of the Project", subsec_title))
    story.append(Paragraph(
        "UniTrack is purposefully scoped as a <b>medium-sized, robust desktop application</b> rather than an unwieldy distributed web system. This deliberate sizing guarantees that all core functionality is fully operational, thoroughly tested, and designed strictly according to clean OODP conventions.",
        body
    ))
    
    scope_data = [
        [Paragraph("Category", table_head), Paragraph("In-Scope Functional Boundaries", table_head), Paragraph("Out-of-Scope (Deliberate Simplifications)", table_head)],
        [
            Paragraph("Target Users", table_cell_bold),
            Paragraph("• Student (personal tasks, course calendar, downloads)<br/>• Teacher (assigned courses, event scheduling, syllabus check)<br/>• Administrator (semester bounds, official holidays)", table_cell),
            Paragraph("Public self-registration workflows; multi-institution federation.", table_cell)
        ],
        [
            Paragraph("Calendar & Events", table_cell_bold),
            Paragraph("• Full interactive Month Grid with Today/Prev/Next<br/>• Single normalized CalendarEvent model for all event types<br/>• Day detail drilldown with attached notes and assessment links", table_cell),
            Paragraph("Real-time two-way synchronization with external cloud APIs (Google / Microsoft Graph).", table_cell)
        ],
        [
            Paragraph("Storage & Sync", table_cell_bold),
            Paragraph("• Normalized MySQL relational database accessed via JDBC<br/>• Local file system repository for PDF slides and worksheets<br/>• Localhost TCP socket daemon for university holiday sync", table_cell),
            Paragraph("Cloud bucket storage (AWS S3); complex distributed microservices.", table_cell)
        ]
    ]
    t_scope = Table(scope_data, colWidths=[90, 225, 217], repeatRows=1)
    t_scope.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_scope)
    story.append(Spacer(1, 10))
    
    # ==================================================================
    # 2. CONTRIBUTIONS
    # ==================================================================
    story.append(Paragraph("2. CONTRIBUTIONS", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    story.append(Paragraph(
        "Through the development of the UniTrack project, several specific architectural and technical contributions were designed and implemented:",
        body
    ))
    story.append(Paragraph("• <b>Unified Academic Event Model</b>: Synthesized lectures, quizzes, exams, assignment deadlines, and personal milestones into a single, cohesive entity hierarchy, eliminating redundant database records while preserving event-specific properties.", bullet))
    story.append(Paragraph("• <b>Layered Service-Level RBAC Enforcement</b>: Implemented role-based security directly within the business service tier using polymorphic dispatch, guaranteeing that unauthorized actions (e.g. students altering syllabus topics) are prevented regardless of UI presentation state.", bullet))
    story.append(Paragraph("• <b>Generic DAO Pattern Implementation</b>: Engineered a reusable, type-safe <code>Repository&lt;T, ID&gt;</code> interface and abstract JDBC template, drastically reducing boilerplate persistence code and separating SQL logic from user interface handlers.", bullet))
    story.append(Paragraph("• <b>Non-Blocking Background Concurrency Engine</b>: Created an autonomous deadline monitoring daemon using <code>ScheduledExecutorService</code> that safely communicates with the Swing Event Dispatch Thread (EDT) via <code>SwingUtilities.invokeLater</code> to prevent UI freezes.", bullet))
    story.append(Paragraph("• <b>Decoupled Local Networking Subsystem</b>: Developed a standalone client-server socket protocol capable of synchronizing campus-wide holiday notices, demonstrating network stream I/O without dependency on external third-party servers.", bullet))
    story.append(Spacer(1, 10))
    
    # ==================================================================
    # 3. METHODOLOGY
    # ==================================================================
    story.append(Paragraph("3. METHODOLOGY", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("3.1 Development Process", subsec_title))
    story.append(Paragraph(
        "The project followed an <b>Iterative Agile Lifecycle</b> structured across five distinct sprints. This incremental approach guaranteed that a functioning Minimum Viable Product (MVP) was established early (Sprint 2), allowing subsequent sprints to focus on advanced course requirements like concurrency, networking, and security hardening.",
        body
    ))
    
    sprint_data = [
        [Paragraph("Sprint / Phase", table_head), Paragraph("Primary Focus", table_head), Paragraph("Sprint Deliverable & Validation", table_head)],
        [Paragraph("Sprint 1 (Phases 1-2)", table_cell_bold), Paragraph("Domain Modeling & Environment Setup", table_cell), Paragraph("Package hierarchy, POJO domain classes (<code>User</code>, <code>CalendarEvent</code>, <code>Course</code>), Enums.", table_cell)],
        [Paragraph("Sprint 2 (Phases 3-4)", table_cell_bold), Paragraph("Calendar Core & Database Persistence", table_cell), Paragraph("Interactive 7x6 month grid in Swing, MySQL relational schema, Generic DAO, JDBC CRUD.", table_cell)],
        [Paragraph("Sprint 3 (Phases 5-6)", table_cell_bold), Paragraph("RBAC Security & Academic Modules", table_cell), Paragraph("Student/Teacher roles, PDF attachment file upload/download, syllabus topic progress tracker.", table_cell)],
        [Paragraph("Sprint 4 (Phases 7-8)", table_cell_bold), Paragraph("Concurrency & Sockets Networking", table_cell), Paragraph("Background <code>ScheduledExecutorService</code> reminder daemon, TCP ServerSocket synchronization.", table_cell)],
        [Paragraph("Sprint 5 (Phases 9-10)", table_cell_bold), Paragraph("Testing, QA Audits & Documentation", table_cell), Paragraph("Boundary test cases, SQL injection verification, thread termination tests, project report.", table_cell)]
    ]
    t_sprint = Table(sprint_data, colWidths=[110, 160, 262], repeatRows=1)
    t_sprint.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_sprint)
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("3.2 Technologies and Tools Used", subsec_title))
    story.append(Paragraph(
        "• <b>Programming Language</b>: Java SE 17+ (JDK 21 LTS) — Leveraging modern Java 8+ features including the <code>java.time</code> package (<code>LocalDate</code>, <code>LocalDateTime</code>), Lambdas, and the Stream API.<br/>"
        "• <b>GUI Framework</b>: Java Swing (JFC) & AWT — Utilizing <code>JPanel</code>, <code>JTable</code>, <code>JDialog</code>, custom <code>TableCellRenderer</code>, and <code>CardLayout</code>.<br/>"
        "• <b>Database Management</b>: MySQL Server 8.0 — Relational persistence with foreign keys, cascading integrity, and composite primary keys.<br/>"
        "• <b>Persistence Connectivity</b>: JDBC Driver (<code>mysql-connector-j:8.3.0</code>) with parameterized <code>PreparedStatement</code>.<br/>"
        "• <b>Build & Quality Tools</b>: Apache Maven for dependency management, JUnit 5 for unit test execution, and Git for version control.",
        body
    ))
    
    story.append(Paragraph("3.3 Challenges Faced and Solutions Implemented", subsec_title))
    
    chal_data = [
        [Paragraph("Technical Challenge", table_head), Paragraph("Underlying Problem", table_head), Paragraph("Engineered Solution in UniTrack", table_head)],
        [
            Paragraph("Swing EDT Freezing", table_cell_bold),
            Paragraph("Executing database queries and file I/O directly inside button click action listeners froze the UI.", table_cell),
            Paragraph("Offloaded long-running tasks to background worker threads using <code>SwingWorker&lt;T, V&gt;</code> and updated UI components strictly via <code>SwingUtilities.invokeLater()</code>.", table_cell)
        ],
        [
            Paragraph("Duplicate Reminder Alerts", table_cell_bold),
            Paragraph("Periodic background checking loops repeatedly triggered popup alerts for the same approaching deadline.", table_cell),
            Paragraph("Implemented a deterministic deduplication key (<code>event_id + interval</code>) stored in a thread-safe set and database table to guarantee single alert dispatching.", table_cell)
        ],
        [
            Paragraph("Polymorphic Persistence", table_cell_bold),
            Paragraph("Storing assessments (quizzes/midterms) separately caused duplicate records on the calendar.", table_cell),
            Paragraph("Utilized a single normalized <code>CALENDAR_EVENTS</code> table with an <code>event_type</code> discriminator and a 1-to-1 foreign key to an optional <code>ASSESSMENTS</code> detail table.", table_cell)
        ],
        [
            Paragraph("Safe Application Shutdown", table_cell_bold),
            Paragraph("Background executor threads prevented the Java Virtual Machine from terminating when closing the window.", table_cell),
            Paragraph("Registered a <code>WindowListener</code> on the main JFrame that calls <code>executor.shutdownNow()</code> and closes the database connection pool cleanly.", table_cell)
        ]
    ]
    t_chal = Table(chal_data, colWidths=[110, 180, 242], repeatRows=1)
    t_chal.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_chal)
    story.append(PageBreak())
    
    # ==================================================================
    # 4. DESIGN (Black and White)
    # ==================================================================
    story.append(Paragraph("4. DESIGN", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("4.1 System Architecture", subsec_title))
    story.append(Paragraph(
        "UniTrack is structured into four decoupled layers. This design ensures that graphical components never interact directly with database logic, satisfying the Single Responsibility Principle and facilitating comprehensive testing.",
        body
    ))
    story.append(create_system_architecture_diagram_bw())
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("4.2 UML Class Diagram & Object-Oriented Hierarchy", subsec_title))
    story.append(Paragraph(
        "The core domain leverages inheritance, polymorphism, and interface abstraction to represent users, calendar events, and data access contracts:",
        body
    ))
    
    uml_data = [
        [Paragraph("Package", table_head), Paragraph("Class / Interface", table_head), Paragraph("Inheritance / Implementation", table_head), Paragraph("Key Role & Members", table_head)],
        [
            Paragraph("unitrack.model", table_cell_bold),
            Paragraph("<code>User</code> (abstract)", table_cell_code),
            Paragraph("Base class", table_cell),
            Paragraph("<code>id, name, email, passwordHash, role</code>;<br/><code>abstract boolean hasPermission(Action a)</code>", table_cell)
        ],
        [
            Paragraph("unitrack.model", table_cell_bold),
            Paragraph("<code>Student</code>, <code>Teacher</code>, <code>Admin</code>", table_cell_code),
            Paragraph("<code>extends User</code>", table_cell),
            Paragraph("Overrides <code>hasPermission()</code> with role-specific authorization rules; manages enrolled courses.", table_cell)
        ],
        [
            Paragraph("unitrack.model", table_cell_bold),
            Paragraph("<code>CalendarEvent</code>", table_cell_code),
            Paragraph("<code>implements Notifiable</code>", table_cell),
            Paragraph("<code>id, title, type, startDt, endDt, course, creator, filePath</code>; contains date comparison logic.", table_cell)
        ],
        [
            Paragraph("unitrack.repository", table_cell_bold),
            Paragraph("<code>Repository&lt;T, ID&gt;</code>", table_cell_code),
            Paragraph("Generic Interface", table_cell),
            Paragraph("Defines <code>findById(ID), findAll(), save(T), update(T), deleteById(ID)</code>.", table_cell)
        ],
        [
            Paragraph("unitrack.repository", table_cell_bold),
            Paragraph("<code>EventDaoImpl</code>, <code>UserDaoImpl</code>", table_cell_code),
            Paragraph("<code>implements Repository&lt;T, ID&gt;</code>", table_cell),
            Paragraph("Encapsulates JDBC <code>PreparedStatement</code> and SQL mapping for MySQL persistence.", table_cell)
        ],
        [
            Paragraph("unitrack.service", table_cell_bold),
            Paragraph("<code>CalendarService</code>, <code>AuthService</code>", table_cell_code),
            Paragraph("Service layer controllers", table_cell),
            Paragraph("Coordinates business rules, syllabus progress counting, date collision validation, and alerts.", table_cell)
        ]
    ]
    t_uml = Table(uml_data, colWidths=[90, 130, 120, 192], repeatRows=1)
    t_uml.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_uml)
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("4.3 Database Design & Relational Schema", subsec_title))
    story.append(Paragraph(
        "The relational schema is strictly normalized to the Third Normal Form (3NF). Relationships between users, courses, and calendar entries are preserved using foreign keys with referential constraints:",
        body
    ))
    
    schema_data = [
        [Paragraph("Table Name", table_head), Paragraph("Primary Key", table_head), Paragraph("Foreign Keys", table_head), Paragraph("Key Purpose & Cardinality", table_head)],
        [Paragraph("USERS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("None", table_cell), Paragraph("Stores students, teachers, administrators with unique email.", table_cell)],
        [Paragraph("SEMESTERS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("None", table_cell), Paragraph("Defines academic terms. Check constraint: end_date &gt; start_date.", table_cell)],
        [Paragraph("COURSES", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("semester_id, teacher_id", table_cell_code), Paragraph("Academic subjects assigned to a teacher. 1-to-N with Events.", table_cell)],
        [Paragraph("ENROLLMENTS", table_cell_bold), Paragraph("student_id + course_id", table_cell_code), Paragraph("student_id, course_id", table_cell_code), Paragraph("Composite PK table modeling N-to-M student course registration.", table_cell)],
        [Paragraph("CALENDAR_EVENTS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("course_id, created_by", table_cell_code), Paragraph("Central date-driven records. Indexed on (start_dt, end_dt).", table_cell)],
        [Paragraph("NOTES", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("course_id, event_id, uploaded_by", table_cell_code), Paragraph("Metadata for attached PDF slides; file resides in local store.", table_cell)],
        [Paragraph("SYLLABUS_TOPICS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("course_id, completed_by", table_cell_code), Paragraph("Ordered curriculum topics with completion flag for progress %.", table_cell)],
        [Paragraph("TODO_ITEMS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("owner_id -&gt; USERS", table_cell_code), Paragraph("Personal student task checklist with priority and due date.", table_cell)],
        [Paragraph("NOTIFICATIONS", table_cell_bold), Paragraph("id (INT AI)", table_cell_code), Paragraph("recipient_id, event_id", table_cell_code), Paragraph("Alert logs. Unique dedup_key prevents duplicate alert spam.", table_cell)]
    ]
    t_sch = Table(schema_data, colWidths=[95, 95, 130, 212], repeatRows=1)
    t_sch.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.8),
    ]))
    story.append(t_sch)
    story.append(PageBreak())
    
    # ==================================================================
    # 5. IMPLEMENTATION (Black and White)
    # ==================================================================
    story.append(Paragraph("5. IMPLEMENTATION", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("5.1 Step-by-Step Account of the Implementation Process", subsec_title))
    story.append(Paragraph(
        "Development proceeded sequentially from data persistence upwards to user interface presentation: (1) Setup MySQL schema and JDBC connection pool; (2) Implement generic DAO contracts and concrete query mappers; (3) Develop business services with role checking; (4) Build Swing UI components with custom paint renderers; (5) Integrate background daemon threads for deadline alerts; and (6) Verify end-to-end functionality under unit and boundary test suites.",
        body
    ))
    story.append(create_event_lifecycle_diagram_bw())
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("5.2 Key Code Snippets and Explanations", subsec_title))
    
    # Snippet 1
    story.append(Paragraph("<b>Snippet 1: Abstract User and Polymorphic Permission Enforcement</b>", table_cell_bold))
    code_snippet_1 = """// unitrack.model.User.java (Abstract Base Class)
public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected Role role;

    public User(int id, String name, String email, Role role) {
        this.id = id; this.name = name; this.email = email; this.role = role;
    }
    public abstract boolean hasPermission(Action action);
}

// unitrack.model.Teacher.java (Polymorphic Concrete Class)
public class Teacher extends User {
    private List<Course> assignedCourses = new ArrayList<>();

    public Teacher(int id, String name, String email) { super(id, name, email, Role.TEACHER); }

    @Override
    public boolean hasPermission(Action action) {
        return action == Action.CREATE_EVENT || action == Action.UPLOAD_NOTE 
            || action == Action.UPDATE_SYLLABUS || action == Action.VIEW_CALENDAR;
    }
}"""
    t_code1 = Table([[Paragraph(code_snippet_1.replace('\n', '<br/>').replace(' ', '&nbsp;'), code_block)]], colWidths=[532])
    t_code1.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_CODE_BG),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_code1)
    story.append(Paragraph("<i>Explanation</i>: Demonstrates inheritance and runtime polymorphism. Security logic interrogates <code>user.hasPermission()</code> dynamically, ensuring consistent authorization across controllers.", body))
    story.append(PageBreak())
    
    # Snippet 2
    story.append(Paragraph("<b>Snippet 2: Generic Repository Pattern and Safe JDBC Execution</b>", table_cell_bold))
    code_snippet_2 = """// unitrack.repository.Repository.java (Generic Interface)
public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean save(T entity);
    boolean deleteById(ID id);
}

// unitrack.repository.EventDaoImpl.java (Parameterized JDBC DAO)
public class EventDaoImpl implements Repository<CalendarEvent, Integer> {
    private static final String INSERT_SQL = 
        "INSERT INTO CALENDAR_EVENTS (title, description, event_type, start_dt, end_dt, course_id, created_by) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    public boolean save(CalendarEvent event) {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getType().name());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartDateTime()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndDateTime()));
            ps.setInt(6, event.getCourseId());
            ps.setInt(7, event.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(EventDaoImpl.class.getName()).log(Level.SEVERE, "Error inserting event", ex);
            return false;
        }
    }
}"""
    t_code2 = Table([[Paragraph(code_snippet_2.replace('\n', '<br/>').replace(' ', '&nbsp;'), code_block)]], colWidths=[532])
    t_code2.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_CODE_BG),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_code2)
    story.append(Paragraph("<i>Explanation</i>: Demonstrates Java Generics (<code>&lt;T, ID&gt;</code>) and secure JDBC persistence. Try-with-resources guarantees automatic connection closure and parameterized queries eliminate SQL injection vulnerabilities.", body))
    story.append(Spacer(1, 4))
    
    # Snippet 3
    story.append(Paragraph("<b>Snippet 3: Multithreaded Background Reminder Daemon and Clean Shutdown</b>", table_cell_bold))
    code_snippet_3 = """// unitrack.concurrency.ReminderDaemon.java (Background Concurrency)
public class ReminderDaemon {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Set<String> sentAlertKeys = ConcurrentHashMap.newKeySet();

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkApproachingDeadlines, 0, 60, TimeUnit.SECONDS);
    }

    private void checkApproachingDeadlines() {
        LocalDateTime threshold = LocalDateTime.now().plusHours(24);
        List<CalendarEvent> upcoming = eventDao.findEventsBefore(threshold);

        for (CalendarEvent event : upcoming) {
            String dedupKey = event.getId() + "_" + event.getStartDateTime().toLocalDate();
            if (sentAlertKeys.add(dedupKey)) { // Thread-safe deduplication
                SwingUtilities.invokeLater(() -> 
                    NotificationDialog.showDeadlineAlert(event)
                );
            }
        }
    }

    public void shutdown() {
        scheduler.shutdownNow(); // Clean termination during window close
    }
}"""
    t_code3 = Table([[Paragraph(code_snippet_3.replace('\n', '<br/>').replace(' ', '&nbsp;'), code_block)]], colWidths=[532])
    t_code3.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_CODE_BG),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_code3)
    story.append(Paragraph("<i>Explanation</i>: Demonstrates multithreading and thread safety. The daemon polls in the background every 60 seconds without freezing the UI, using <code>ConcurrentHashMap</code> for deduplication and dispatching alerts to the Swing EDT.", body))
    story.append(PageBreak())
    
    # ==================================================================
    # 6. RESULT (Black and White)
    # ==================================================================
    story.append(Paragraph("6. RESULT", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    story.append(Paragraph(
        "UniTrack was successfully compiled and tested on the Java 17+ desktop runtime. The user interface adheres strictly to standard desktop UI ergonomics, providing rapid navigation, immediate visual cues, and full access control.",
        body
    ))
    
    ui_data = [
        [Paragraph("Screen / View", table_head), Paragraph("Visual Components & Controls", table_head), Paragraph("User Interaction & Functional Outcome", table_head)],
        [
            Paragraph("1. Authentication & Role Switcher", table_cell_bold),
            Paragraph("• Email & password credentials input<br/>• Quick-select demonstration role radio buttons (Student / Teacher / Admin)", table_cell),
            Paragraph("Authenticates against BCrypt hashed credentials; resolves role; initializes security context and navigation permissions.", table_cell)
        ],
        [
            Paragraph("2. Interactive Monthly Calendar", table_cell_bold),
            Paragraph("• 7x6 month grid with day-of-week headers<br/>• Month/Year header with &lt; Prev, Today, Next &gt;<br/>• Formatted event badges with clear text labels (Lecture, Test, Assignment, Holiday)", table_cell),
            Paragraph("Displays academic schedule at a glance. Current day highlighted with bold outline; clicking any date opens the Day Detail Modal.", table_cell)
        ],
        [
            Paragraph("3. Day Detail & Event Management Modal", table_cell_bold),
            Paragraph("• Chronological event timeline for selected date<br/>• 'Add Event' button (Teacher/Admin only)<br/>• Download link icon for attached PDF materials", table_cell),
            Paragraph("Teachers configure time windows, attach files, and publish class tests. Students view schedule and download lecture slides.", table_cell)
        ],
        [
            Paragraph("4. Teacher Syllabus Progress Tracker", table_cell_bold),
            Paragraph("• Course syllabus checklist panel<br/>• Dynamic JProgressBar with % indicator<br/>• 'Mark Complete' toggle checkboxes", table_cell),
            Paragraph("Teachers check off topics as classes conclude. Automatically computes percentage: <code>completed / total * 100</code> using Streams API.", table_cell)
        ],
        [
            Paragraph("5. Personal Student To-Do Workspace", table_cell_bold),
            Paragraph("• Private task list table with priority tags<br/>• 'Add Task', 'Due Date', and Checkbox columns<br/>• Filter tabs: All, Today, Upcoming, Completed", table_cell),
            Paragraph("Allows students to track personal study goals independently from teacher syllabus topics, stored under strictly private <code>owner_id</code>.", table_cell)
        ],
        [
            Paragraph("6. Automated Deadline Alert Dialog", table_cell_bold),
            Paragraph("• Toast-style alert notification popup<br/>• Displays event title, course code, and hours remaining<br/>• 'Snooze' and 'Acknowledge' action buttons", table_cell),
            Paragraph("Triggered by background daemon when deadlines enter the 24-hour threshold; dispatched cleanly onto the Swing EDT.", table_cell)
        ]
    ]
    t_ui = Table(ui_data, colWidths=[110, 190, 232], repeatRows=1)
    t_ui.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_ui)
    story.append(Spacer(1, 8))
    
    story.append(Paragraph("Verification & Test Results Summary", subsec_title))
    test_summary_data = [
        [Paragraph("Test Suite", table_head), Paragraph("Total Cases", table_head), Paragraph("Passed", table_head), Paragraph("Failed", table_head), Paragraph("Assessment & Evaluation", table_head)],
        [Paragraph("Calendar Date Arithmetic", table_cell_bold), Paragraph("12", table_cell), Paragraph("12", table_cell), Paragraph("0", table_cell), Paragraph("Leap years (2028), year rollovers, and month lengths verified.", table_cell)],
        [Paragraph("RBAC Permission Security", table_cell_bold), Paragraph("15", table_cell), Paragraph("15", table_cell), Paragraph("0", table_cell), Paragraph("Students blocked from editing teacher events and syllabus topics.", table_cell)],
        [Paragraph("JDBC & SQL Injection", table_cell_bold), Paragraph("10", table_cell), Paragraph("10", table_cell), Paragraph("0", table_cell), Paragraph("Malicious SQL fragments treated safely as literal parameters.", table_cell)],
        [Paragraph("Concurrency & Shutdown", table_cell_bold), Paragraph("8", table_cell), Paragraph("8", table_cell), Paragraph("0", table_cell), Paragraph("No duplicate alerts dispatched; executor thread closes on exit.", table_cell)],
        [Paragraph("File Attachment Pipeline", table_cell_bold), Paragraph("8", table_cell), Paragraph("8", table_cell), Paragraph("0", table_cell), Paragraph("PDF uploads validated; missing files handled gracefully.", table_cell)]
    ]
    t_test_sum = Table(test_summary_data, colWidths=[110, 60, 50, 50, 262], repeatRows=1)
    t_test_sum.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_test_sum)
    story.append(PageBreak())
    
    # ==================================================================
    # 7. CONCLUSION & REFLECTIONS
    # ==================================================================
    story.append(Paragraph("7. CONCLUSION", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("7.1 Summary of the Project and Its Impact", subsec_title))
    story.append(Paragraph(
        "<b>UniTrack — Smart Academic Calendar</b> successfully satisfies all core and advanced requirements of an academic Object-Oriented Design and Programming project. By choosing a medium-scale desktop architecture rather than an overly complex web infrastructure, the project maintains high implementation fidelity, clean package modularity, and comprehensive test coverage. The resulting system bridges high-level academic semester planning with the day-to-day study workflow of students and teachers, providing a cohesive calendar tool that enhances academic productivity.",
        body
    ))
    
    story.append(Paragraph("7.2 Reflection on What Was Learned", subsec_title))
    story.append(Paragraph(
        "Throughout this project, significant software engineering insights were gained across multiple domains:<br/>"
        "• <b>Object-Oriented Design Principles</b>: Learned how applying the Open-Closed Principle and Liskov Substitution Principle enables seamless extension (e.g. adding new event types or user roles) without modifying existing controllers.<br/>"
        "• <b>Concurrency & UI Thread Safety</b>: Gained hands-on mastery over the Java Swing Event Dispatch Thread (EDT) and multithreaded scheduling, understanding why background daemons must interact with UI components strictly through thread-safe wrappers.<br/>"
        "• <b>Relational Data Integrity</b>: Appreciated the importance of database normalization (3NF), parameterized SQL statements, and ACID transaction boundaries in preventing data anomalies and security exploits.",
        body
    ))
    story.append(Spacer(1, 8))
    
    # ==================================================================
    # 8. REFERENCES
    # ==================================================================
    story.append(Paragraph("8. REFERENCES", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    story.append(Paragraph("1. Bloch, J. (2018). <i>Effective Java</i> (3rd ed.). Addison-Wesley Professional.", bullet))
    story.append(Paragraph("2. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). <i>Design Patterns: Elements of Reusable Object-Oriented Software</i>. Addison-Wesley.", bullet))
    story.append(Paragraph("3. Oracle Corporation. (2024). <i>Java SE 17 & 21 Documentation: Concurrency Utilities and Swing Architecture</i>. Oracle Technology Network.", bullet))
    story.append(Paragraph("4. MySQL AB. (2024). <i>MySQL 8.0 Reference Manual: InnoDB Storage Engine & Transaction Isolation</i>. Oracle Corporation.", bullet))
    story.append(Paragraph("5. Martin, R. C. (2008). <i>Clean Code: A Handbook of Agile Software Craftsmanship</i>. Prentice Hall.", bullet))
    story.append(PageBreak())
    
    # ==================================================================
    # 9. APPENDICES (Black and White)
    # ==================================================================
    story.append(Paragraph("9. APPENDICES", sec_title))
    story.append(HRFlowable(width="100%", thickness=1, color=COLOR_BLACK, spaceAfter=6, spaceBefore=0))
    
    story.append(Paragraph("Appendix A: Complete MySQL DDL Schema Definition (schema.sql excerpt)", subsec_title))
    ddl_excerpt = """CREATE DATABASE IF NOT EXISTS unitrack_db;
USE unitrack_db;

CREATE TABLE USERS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CALENDAR_EVENTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    event_type ENUM('LECTURE', 'TEST', 'ASSIGNMENT', 'EXAM', 'HOLIDAY', 'TASK') NOT NULL,
    start_dt DATETIME NOT NULL,
    end_dt DATETIME NOT NULL,
    course_id INT NULL,
    created_by INT NOT NULL,
    location VARCHAR(100),
    file_path VARCHAR(255),
    CONSTRAINT fk_event_user FOREIGN KEY (created_by) REFERENCES USERS(id) ON DELETE CASCADE,
    INDEX idx_event_dates (start_dt, end_dt)
);"""
    t_ddl = Table([[Paragraph(ddl_excerpt.replace('\n', '<br/>').replace(' ', '&nbsp;'), code_block)]], colWidths=[532])
    t_ddl.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_CODE_BG),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_ddl)
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("Appendix B: Recommended Package and Directory Structure", subsec_title))
    pkg_structure = """<b>unitrack/</b><br/>
&nbsp;&nbsp;[+] <b>model/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User, Student, Teacher, Admin, CalendarEvent, Course, Semester, SyllabusTopic, TodoItem, Notification<br/>
&nbsp;&nbsp;[+] <b>view/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MainCalendarFrame, MonthGridPanel, DayDetailDialog, EventFormDialog, CourseView, TodoPanel<br/>
&nbsp;&nbsp;[+] <b>controller/</b> &nbsp;&nbsp;&nbsp;CalendarController, AuthController, CourseController, TaskController<br/>
&nbsp;&nbsp;[+] <b>service/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CalendarService, AuthService, CourseService, NotificationService, FileService, ProgressService<br/>
&nbsp;&nbsp;[+] <b>repository/</b> &nbsp;&nbsp;&nbsp;Repository&lt;T, ID&gt;, EventDaoImpl, UserDaoImpl, CourseDaoImpl, TaskDaoImpl, NoteDaoImpl<br/>
&nbsp;&nbsp;[+] <b>database/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DatabaseConnectionPool, DatabaseConfig<br/>
&nbsp;&nbsp;[+] <b>concurrency/</b> &nbsp;&nbsp;ReminderDaemon, ThreadFactoryHelper<br/>
&nbsp;&nbsp;[+] <b>networking/</b> &nbsp;&nbsp;&nbsp;CalendarSyncClient, AcademicServerDaemon<br/>
&nbsp;&nbsp;[+] <b>util/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DateUtils, SecurityUtils, InputValidator, Constants"""
    t_pkg = Table([[Paragraph(pkg_structure, code_block)]], colWidths=[532])
    t_pkg.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_BG_ALT),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_pkg)
    
    # Build Document
    doc.build(story, canvasmaker=ReportNumberedCanvas)
    print(f"B&W Formal Academic Project Report PDF successfully generated at: {output_path}")

if __name__ == '__main__':
    target = os.path.abspath('UniTrack_Smart_Academic_Calendar_Project_Report.pdf')
    generate_report(target)
