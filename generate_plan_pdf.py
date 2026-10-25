"""
UniTrack — Smart Academic Calendar: OODP Project Plan PDF Generator (Strict Black & White Edition)
Generates an executive, section-wise project plan document with tables,
vector flowcharts, data dictionaries, and architectural diagrams using ONLY Black and White.
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
class NumberedCanvas(canvas.Canvas):
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
        self.saveState()
        page_num = self._pageNumber
        
        # Header (Top) - Black & White
        self.setFont('Helvetica-Bold', 8)
        self.setFillColor(colors.black)
        self.drawString(36, 758, 'UNITRACK — SMART ACADEMIC CALENDAR')
        
        self.setFont('Helvetica', 8)
        self.setFillColor(colors.HexColor('#444444'))
        self.drawRightString(576, 758, 'OODP Academic Project Plan | Java / OOP')
        
        self.setStrokeColor(colors.black)
        self.setLineWidth(0.75)
        self.line(36, 750, 576, 750)
        
        # Footer (Bottom) - Black & White
        self.line(36, 38, 576, 38)
        self.setFont('Helvetica', 8)
        self.setFillColor(colors.HexColor('#444444'))
        self.drawString(36, 26, 'Department of Computer Science & Engineering • Object-Oriented Design & Programming')
        
        page_str = f'Page {page_num} of {total_pages}'
        self.setFont('Helvetica-Bold', 8)
        self.setFillColor(colors.black)
        self.drawRightString(576, 26, page_str)
        
        self.restoreState()


# ----------------------------------------------------------------------
# Flowchart Helper Drawing Functions (Strict Black & White)
# ----------------------------------------------------------------------
def draw_arrow_right_bw(d, x1, y, x2, color=colors.black, width=1.3):
    d.add(Line(x1, y, x2, y, strokeColor=color, strokeWidth=width))
    d.add(Polygon([x2, y, x2 - 5.5, y + 3, x2 - 5.5, y - 3], fillColor=color, strokeColor=None))

def create_system_architecture_diagram_bw():
    dw = 540
    dh = 105
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    d.add(Rect(8, dh - 16, 150, 13, fillColor=colors.HexColor('#EEEEEE'), strokeColor=colors.black, strokeWidth=0.5, rx=2, ry=2))
    d.add(String(14, dh - 12.5, 'LAYERED ARCHITECTURAL FLOW', fontName='Helvetica-Bold', fontSize=7.2, fillColor=colors.black))
    
    tier_width = 110
    tier_height = 72
    y_pos = 11
    spacing = 25
    x_start = 12
    
    tiers = [
        ('1. Presentation', ['Swing Desktop GUI', 'Event Dispatch Thread', 'Optional JSP / Web']),
        ('2. Service Layer', ['CalendarService', 'Auth & Permission Logic', 'Notification Engine']),
        ('3. Data Access', ['Generic Repository<T>', 'Concrete DAOs', 'PreparedStatement']),
        ('4. Infrastructure', ['MySQL Database', 'ScheduledExecutor', 'Local File Storage / I/O'])
    ]
    
    for i, (title, items) in enumerate(tiers):
        x = x_start + i * (tier_width + spacing)
        d.add(Rect(x, y_pos, tier_width, tier_height, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + tier_width / 2, y_pos + 57, title, fontName='Helvetica-Bold', fontSize=8, fillColor=colors.black, textAnchor='middle'))
        d.add(Line(x + 8, y_pos + 52, x + tier_width - 8, y_pos + 52, strokeColor=colors.HexColor('#666666'), strokeWidth=0.5))
        
        for j, item in enumerate(items):
            d.add(String(x + tier_width / 2, y_pos + 39 - (j * 12), item, fontName='Helvetica', fontSize=6.5, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(tiers) - 1:
            arr_x1 = x + tier_width
            arr_x2 = arr_x1 + spacing
            arr_y = y_pos + tier_height / 2
            draw_arrow_right_bw(d, arr_x1, arr_y, arr_x2, color=colors.black, width=1.3)
            # return flow dashed
            d.add(Line(arr_x2 - 3, arr_y - 10, arr_x1 + 3, arr_y - 10, strokeColor=colors.HexColor('#666666'), strokeWidth=0.8, strokeDashArray=[2, 2]))
            d.add(Polygon([arr_x1 + 3, arr_y - 10, arr_x1 + 6.5, arr_y - 7.5, arr_x1 + 6.5, arr_y - 12.5], fillColor=colors.HexColor('#666666'), strokeColor=None))
            
    return d

def create_rbac_routing_diagram_bw():
    dw = 540
    dh = 75
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    steps = [
        ('1. User Login', 'Credentials & Email\nInput via Swing Form'),
        ('2. Auth Service', 'BCrypt Hash Verify\n& Role Resolution'),
        ('3. Polymorphic User', 'Instantiates Student,\nTeacher, or Admin'),
        ('4. Security Context', 'Session registers User\n& permissions matrix'),
        ('5. Role Dashboard', 'UI renders customized\nviews & action toolbars')
    ]
    
    box_w = 84
    box_h = 52
    y_pos = 11
    spacing = 23
    x_start = 14
    
    for i, (title, subtitle) in enumerate(steps):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 38, title, fontName='Helvetica-Bold', fontSize=7.6, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 25 - (k * 10), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(steps) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d

def create_event_lifecycle_diagram_bw():
    dw = 540
    dh = 75
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    steps = [
        ('1. User Input', 'Teacher/Student/Admin\nSubmits Event Form'),
        ('2. Validation', 'Service checks rules\n& date integrity'),
        ('3. DB Persistence', 'EventDAO executes\nPreparedStatement'),
        ('4. Scheduler Sync', 'Background Executor\nregisters reminder'),
        ('5. UI Grid Refresh', 'Swing EDT updates\nCalendar Month View')
    ]
    
    box_w = 84
    box_h = 52
    y_pos = 11
    spacing = 23
    x_start = 14
    
    for i, (title, subtitle) in enumerate(steps):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 38, title, fontName='Helvetica-Bold', fontSize=7.6, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 25 - (k * 10), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(steps) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d

def create_concurrency_diagram_bw():
    dw = 540
    dh = 75
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    steps = [
        ('Daemon Started', 'ScheduledExecutorService\nruns on startup (60s loop)'),
        ('Query Approaching', 'EventDAO polls events\nwith deadline in <= 24h'),
        ('Deduplication', 'Check sent_status key\nto avoid repeated alerts'),
        ('EDT Dispatch', 'SwingUtilities.invokeLater\npushes alert dialog to UI'),
        ('Clean Shutdown', 'Executor.shutdownNow()\non window close event')
    ]
    
    box_w = 84
    box_h = 52
    y_pos = 11
    spacing = 23
    x_start = 14
    
    for i, (title, subtitle) in enumerate(steps):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 38, title, fontName='Helvetica-Bold', fontSize=7.6, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 25 - (k * 10), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(steps) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d

def create_networking_diagram_bw():
    dw = 540
    dh = 72
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    # Client box
    d.add(Rect(14, 10, 130, 52, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
    d.add(String(79, 47, 'UniTrack Client', fontName='Helvetica-Bold', fontSize=8, fillColor=colors.black, textAnchor='middle'))
    d.add(String(79, 34, 'Socket / HTTP Feed Client', fontName='Helvetica', fontSize=6.8, fillColor=colors.HexColor('#444444'), textAnchor='middle'))
    d.add(String(79, 21, 'Local Dynamic Port', fontName='Helvetica', fontSize=6.3, fillColor=colors.HexColor('#666666'), textAnchor='middle'))
    
    # Middle network channel
    d.add(Rect(184, 10, 172, 52, fillColor=colors.white, strokeColor=colors.HexColor('#888888'), strokeWidth=0.8, rx=3, ry=3))
    d.add(String(270, 47, 'TCP Socket / Localhost Stream', fontName='Helvetica-Bold', fontSize=7.5, fillColor=colors.black, textAnchor='middle'))
    d.add(String(270, 34, 'GET_ACADEMIC_EVENTS_FEED', fontName='Courier-Bold', fontSize=6.8, fillColor=colors.black, textAnchor='middle'))
    d.add(String(270, 21, 'JSON / Serialized Event Stream', fontName='Courier', fontSize=6.3, fillColor=colors.HexColor('#555555'), textAnchor='middle'))
    
    # Server box
    d.add(Rect(396, 10, 130, 52, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
    d.add(String(461, 47, 'Academic Server Daemon', fontName='Helvetica-Bold', fontSize=8, fillColor=colors.black, textAnchor='middle'))
    d.add(String(461, 34, 'ServerSocket (Port 8080)', fontName='Helvetica', fontSize=6.8, fillColor=colors.HexColor('#444444'), textAnchor='middle'))
    d.add(String(461, 21, 'Queries DB / External Feed', fontName='Helvetica', fontSize=6.3, fillColor=colors.HexColor('#666666'), textAnchor='middle'))
    
    # Request arrow
    draw_arrow_right_bw(d, 144, 41, 184, color=colors.black, width=1.3)
    # Response arrow
    d.add(Line(396, 25, 356, 25, strokeColor=colors.black, strokeWidth=1.3))
    d.add(Polygon([356, 25, 361, 28, 361, 22], fillColor=colors.black, strokeColor=None))
    
    return d

def create_pipeline_diagram_bw():
    dw = 540
    dh = 72
    d = Drawing(dw, dh)
    d.add(Rect(0, 0, dw, dh, fillColor=colors.white, strokeColor=colors.black, strokeWidth=0.75, rx=4, ry=4))
    
    milestones = [
        ('Sprint 1 (P1-P2)', 'Requirements &\nDomain Models'),
        ('Sprint 2 (P3-P4)', 'Swing UI Core &\nJDBC Database'),
        ('Sprint 3 (P5-P6)', 'RBAC Security &\nAcademic Feats'),
        ('Sprint 4 (P7-P8)', 'Concurrency &\nSocket Network'),
        ('Sprint 5 (P9-P10)', 'Servlets (Opt) &\nFinal QA Report')
    ]
    
    box_w = 84
    box_h = 52
    y_pos = 10
    spacing = 23
    x_start = 14
    
    for i, (title, subtitle) in enumerate(milestones):
        x = x_start + i * (box_w + spacing)
        d.add(Rect(x, y_pos, box_w, box_h, fillColor=colors.HexColor('#F8F8F8'), strokeColor=colors.black, strokeWidth=0.8, rx=3, ry=3))
        d.add(String(x + box_w / 2, y_pos + 38, title, fontName='Helvetica-Bold', fontSize=7.4, fillColor=colors.black, textAnchor='middle'))
        
        lines = subtitle.split('\n')
        for k, line in enumerate(lines):
            d.add(String(x + box_w / 2, y_pos + 25 - (k * 10), line, fontName='Helvetica', fontSize=6.2, fillColor=colors.HexColor('#333333'), textAnchor='middle'))
            
        if i < len(milestones) - 1:
            draw_arrow_right_bw(d, x + box_w, y_pos + box_h / 2, x + box_w + spacing, color=colors.black, width=1.2)
            
    return d


# ----------------------------------------------------------------------
# PDF Document Construction (Strict Black & White)
# ----------------------------------------------------------------------
def generate_pdf(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        leftMargin=36,
        rightMargin=36,
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
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=COLOR_BLACK,
        spaceAfter=3
    )
    
    sec_heading = ParagraphStyle(
        'SecHeading',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11.5,
        leading=15,
        textColor=COLOR_BLACK,
        spaceBefore=11,
        spaceAfter=4,
        keepWithNext=True
    )
    
    subsec_heading = ParagraphStyle(
        'SubSecHeading',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9.2,
        leading=12,
        textColor=COLOR_BLACK,
        spaceBefore=8,
        spaceAfter=3,
        keepWithNext=True
    )
    
    body = ParagraphStyle(
        'DocBody',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.2,
        leading=11.5,
        textColor=COLOR_BLACK,
        spaceAfter=5
    )
    
    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=7.6,
        leading=10.2,
        textColor=COLOR_BLACK
    )
    
    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.6,
        leading=10.2,
        textColor=COLOR_BLACK
    )
    
    table_cell_code = ParagraphStyle(
        'TableCellCode',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.1,
        leading=9.2,
        textColor=COLOR_BLACK
    )
    
    table_head = ParagraphStyle(
        'TableHead',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.8,
        leading=10.2,
        textColor=colors.white
    )
    
    story = []
    
    # ------------------------------------------------------------------
    # Document Header / Banner Block (Black and White)
    # ------------------------------------------------------------------
    header_table_data = [
        [
            Paragraph("<b>UNITRACK — SMART ACADEMIC CALENDAR</b><br/><font size=8.5 color='#444444'>Object-Oriented Design & Programming (OODP) Comprehensive Project Plan</font>", title_style),
            Paragraph("<b>Version:</b> 2.0 (B&W Edition)<br/><b>Stack:</b> Java 17+, Swing, JDBC, MySQL<br/><b>Deliverable:</b> Academic Term Submission", table_cell_bold)
        ]
    ]
    header_table = Table(header_table_data, colWidths=[375, 165])
    header_table.setStyle(TableStyle([
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LINEBELOW', (0, 0), (-1, -1), 1.5, COLOR_BLACK)
    ]))
    story.append(header_table)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 1: Project Overview & Executive Summary
    # ------------------------------------------------------------------
    story.append(Paragraph("1. PROJECT OVERVIEW & EXECUTIVE SUMMARY", sec_heading))
    story.append(Paragraph(
        "<b>UniTrack — Smart Academic Calendar</b> is a calendar-centric desktop management system engineered for university students, educators, and academic administrators. Rather than treating courses, exams, and notes as disconnected records, UniTrack centers all academic interactions around a dynamic, date-driven calendar interface. The platform bridges high-level semester scheduling with micro-level daily study tasks, providing a unified workspace that complements university learning systems.",
        body
    ))
    
    tech_data = [
        [Paragraph("Component / Domain", table_head), Paragraph("Technology / Framework", table_head), Paragraph("OODP / Syllabus Focus", table_head), Paragraph("Architectural Role", table_head)],
        [Paragraph("Language & Runtime", table_cell_bold), Paragraph("Java SE 17+ (JDK 21 LTS)", table_cell), Paragraph("Core OOP, Generics, Stream API", table_cell), Paragraph("Primary application codebase", table_cell)],
        [Paragraph("Presentation Layer", table_cell_bold), Paragraph("Java Swing (AWT / JFC)", table_cell), Paragraph("GUI Event Dispatch Thread (EDT)", table_cell), Paragraph("Interactive month/week/day calendar grid", table_cell)],
        [Paragraph("Persistence & DB", table_cell_bold), Paragraph("MySQL 8.0+ via JDBC", table_cell), Paragraph("PreparedStatement, Transactions", table_cell), Paragraph("Normalized relational academic store", table_cell)],
        [Paragraph("Concurrency Engine", table_cell_bold), Paragraph("ScheduledExecutorService", table_cell), Paragraph("Multithreading, Safe State, Locks", table_cell), Paragraph("Background deadline reminder daemon", table_cell)],
        [Paragraph("Network Integration", table_cell_bold), Paragraph("Java Sockets / URL Feed API", table_cell), Paragraph("Networking, I/O Streams, Client-Server", table_cell), Paragraph("Academic holiday feed / sync module", table_cell)],
        [Paragraph("Web Tier (Optional)", table_cell_bold), Paragraph("Servlets 5.0 / JSP & Tomcat", table_cell), Paragraph("MVC Web Pattern, Session State", table_cell), Paragraph("Auxiliary browser view if mandated", table_cell)]
    ]
    t_tech = Table(tech_data, colWidths=[110, 130, 150, 150], repeatRows=1)
    t_tech.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_tech)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 2: Architecture & System Flow
    # ------------------------------------------------------------------
    story.append(Paragraph("2. SYSTEM ARCHITECTURE & DATA FLOW", sec_heading))
    story.append(Paragraph(
        "UniTrack employs a strictly decoupled <b>Multi-Tier Layered Architecture</b>. This separation of concerns enforces object-oriented maintainability: UI event handlers in Swing never execute raw SQL statements; instead, they communicate through cohesive service boundaries which orchestrate generic data repositories and background threads.",
        body
    ))
    story.append(create_system_architecture_diagram_bw())
    story.append(Spacer(1, 6))
    
    arch_data = [
        [Paragraph("Layer", table_head), Paragraph("Key Java Classes / Packages", table_head), Paragraph("Primary Responsibilities", table_head), Paragraph("Coupling Rules", table_head)],
        [
            Paragraph("Presentation", table_cell_bold),
            Paragraph("<font name='Courier'>unitrack.view.*<br/>unitrack.controller.*</font>", table_cell_code),
            Paragraph("Renders calendar grid, dialogs, course forms, and file choosers; delegates clicks to controllers.", table_cell),
            Paragraph("Calls Service layer only. Must NEVER import java.sql or execute DB queries.", table_cell)
        ],
        [
            Paragraph("Service (Business)", table_cell_bold),
            Paragraph("<font name='Courier'>unitrack.service.*<br/>(CalendarService, etc.)</font>", table_cell_code),
            Paragraph("Implements permission checks, syllabus percentage metrics, event validation, and reminder rules.", table_cell),
            Paragraph("Calls Repositories/DAOs; encapsulates all business logic and concurrency triggers.", table_cell)
        ],
        [
            Paragraph("Data Access (DAO)", table_cell_bold),
            Paragraph("<font name='Courier'>unitrack.repository.*<br/>(Repository&lt;T, ID&gt;)</font>", table_cell_code),
            Paragraph("Executes parameterized JDBC queries, manages transaction commit/rollback, maps ResultSet to POJOs.", table_cell),
            Paragraph("Isolated behind Generic Repository interfaces; hides database vendor specifics.", table_cell)
        ],
        [
            Paragraph("Domain Model", table_cell_bold),
            Paragraph("<font name='Courier'>unitrack.model.*<br/>(User, Event, Course)</font>", table_cell_code),
            Paragraph("Encapsulates domain entities, enums (Role, EventType, Priority), and validation constraints.", table_cell),
            Paragraph("Shared across all layers; pure Java objects with getters/setters and immutability where fit.", table_cell)
        ]
    ]
    t_arch = Table(arch_data, colWidths=[90, 130, 190, 130], repeatRows=1)
    t_arch.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_arch)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 3: Target Users & Role-Based Access Control (RBAC)
    # ------------------------------------------------------------------
    story.append(Paragraph("3. TARGET USERS & ROLE-BASED ACCESS CONTROL (RBAC)", sec_heading))
    story.append(Paragraph(
        "UniTrack implements strict Role-Based Access Control enforced at the <b>Service Layer</b>, ensuring that permission checks do not merely rely on enabling/disabling UI buttons. Below is the authentication and role-routing flowchart:",
        body
    ))
    story.append(create_rbac_routing_diagram_bw())
    story.append(Spacer(1, 6))
    
    perm_data = [
        [Paragraph("Feature / Capability", table_head), Paragraph("Student", table_head), Paragraph("Teacher", table_head), Paragraph("Administrator", table_head), Paragraph("Security Enforcement Rule", table_head)],
        [Paragraph("View Academic Calendar", table_cell_bold), Paragraph("Yes (Enrolled Courses)", table_cell), Paragraph("Yes (Assigned Courses)", table_cell), Paragraph("Yes (All Courses / Global)", table_cell), Paragraph("Filtered by SQL JOIN on user role", table_cell)],
        [Paragraph("Manage Official University Events", table_cell_bold), Paragraph("No (Read-Only)", table_cell), Paragraph("No (Read-Only)", table_cell), Paragraph("Yes (Full CRUD)", table_cell), Paragraph("Admin-only Service check", table_cell)],
        [Paragraph("Create Lecture / Class Test", table_cell_bold), Paragraph("No", table_cell), Paragraph("Yes (Assigned Courses)", table_cell), Paragraph("Yes (Administrative)", table_cell), Paragraph("Verify teacher_id == current_user.id", table_cell)],
        [Paragraph("Upload PDF Course Materials", table_cell_bold), Paragraph("No", table_cell), Paragraph("Yes (Assigned Courses)", table_cell), Paragraph("Yes", table_cell), Paragraph("FileService validates MIME & size", table_cell)],
        [Paragraph("Download Attached Notes", table_cell_bold), Paragraph("Yes (Enrolled Courses)", table_cell), Paragraph("Yes (Own Courses)", table_cell), Paragraph("Yes (Global)", table_cell), Paragraph("Verify student enrollment in course", table_cell)],
        [Paragraph("Update Syllabus Topic Status", table_cell_bold), Paragraph("No (View Only)", table_cell), Paragraph("Yes (Mark complete)", table_cell), Paragraph("Yes", table_cell), Paragraph("Teachers only; separate from personal tasks", table_cell)],
        [Paragraph("Manage Personal To-Do Tasks", table_cell_bold), Paragraph("Yes (Private)", table_cell), Paragraph("Yes (Private)", table_cell), Paragraph("Yes (Private)", table_cell), Paragraph("owner_id enforced on task queries", table_cell)],
        [Paragraph("Configure Semester Dates", table_cell_bold), Paragraph("No", table_cell), Paragraph("No", table_cell), Paragraph("Yes", table_cell), Paragraph("Service rejects non-admin role", table_cell)]
    ]
    t_perm = Table(perm_data, colWidths=[130, 80, 85, 95, 150], repeatRows=1)
    t_perm.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_perm)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 4: Core Functional Modules & Feature Breakdown
    # ------------------------------------------------------------------
    story.append(Paragraph("4. CORE FUNCTIONAL MODULES & SPECIFICATIONS", sec_heading))
    story.append(Paragraph(
        "The project is decomposed into 10 cohesive functional modules. All calendar data relies on a unified, normalized <font name='Courier'>CalendarEvent</font> model so assessments and lectures do not duplicate scheduling records.",
        body
    ))
    
    feat_data = [
        [Paragraph("Module", table_head), Paragraph("Key Features & Capabilities", table_head), Paragraph("UI & Controller Elements", table_head), Paragraph("Underlying OODP Mechanism", table_head)],
        [
            Paragraph("4.1 Calendar Core", table_cell_bold),
            Paragraph("Month grid, Previous/Next/Today navigation, selected date cell highlight, event badges, week/day drilldown.", table_cell),
            Paragraph("CalendarPanel, MonthViewGrid, DayDetailDialog, EventBadgeRenderer", table_cell),
            Paragraph("Java 8 Date/Time (LocalDate), Custom Swing 2D Graphics Painting", table_cell)
        ],
        [
            Paragraph("4.2 Academic Events", table_cell_bold),
            Paragraph("Lectures, tests, assignments, exams, holidays, personal tasks. Supports time windows, locations, and courses.", table_cell),
            Paragraph("EventDialog, EventListPanel, EventFilterComboBox", table_cell),
            Paragraph("Polymorphism via EventType enum, Encapsulation of start/end time", table_cell)
        ],
        [
            Paragraph("4.3 Course Manager", table_cell_bold),
            Paragraph("Course codes, titles, assigned teachers, student enrollment mappings, semester bindings.", table_cell),
            Paragraph("CourseManageView, EnrollmentDialog, CourseCardComponent", table_cell),
            Paragraph("1-to-N and N-to-M entity relationships in OOP and DB", table_cell)
        ],
        [
            Paragraph("4.4 Notes & Materials", table_cell_bold),
            Paragraph("Teachers attach lecture slides/PDFs to specific dates or courses; students download authorized files.", table_cell),
            Paragraph("JFileChooser, AttachmentListRenderer, DownloadProgressBar", table_cell),
            Paragraph("Java NIO Files, I/O Streams, Storage abstraction via FileService", table_cell)
        ],
        [
            Paragraph("4.5 Assessments", table_cell_bold),
            Paragraph("Quizzes, Midterms, Assignments with instructions and deadlines directly represented on the calendar.", table_cell),
            Paragraph("AssessmentDetailView, DeadlineCountBadge, OverdueAlertPanel", table_cell),
            Paragraph("Specialized EventType with optional assessment detail records", table_cell)
        ],
        [
            Paragraph("4.6 Syllabus Progress", table_cell_bold),
            Paragraph("Teachers define ordered course topics and toggle completion; calculates exact syllabus progress percentage.", table_cell),
            Paragraph("SyllabusTopicList, JProgressBar, TopicCompletionCheckBox", table_cell),
            Paragraph("Streams API for counting: completed / total * 100", table_cell)
        ],
        [
            Paragraph("4.7 Personal To-Do", table_cell_bold),
            Paragraph("Private student task management: title, due date, priority (High/Med/Low), completion checkbox.", table_cell),
            Paragraph("TodoPanel, TaskCellEditor, TaskFilterTabs (Today/Upcoming)", table_cell),
            Paragraph("Collections sorting via Comparator, Separate user-scoped data", table_cell)
        ],
        [
            Paragraph("4.8 Semester Analytics", table_cell_bold),
            Paragraph("Semester start/end dates, remaining days counter, timeline overview of upcoming milestones.", table_cell),
            Paragraph("SemesterBannerPanel, CountdownWidget, SemesterSummaryView", table_cell),
            Paragraph("ChronoUnit.DAYS.between(LocalDate.now(), semesterEnd)", table_cell)
        ],
        [
            Paragraph("4.9 Reminders & Alerts", table_cell_bold),
            Paragraph("Automated alerts for approaching tests and assignments (e.g. 24h prior) without duplicate spam.", table_cell),
            Paragraph("NotificationTrayIcon, InAppNotificationPopup, AlertBadge", table_cell),
            Paragraph("ScheduledExecutorService, Thread-safe deduplication Map", table_cell)
        ],
        [
            Paragraph("4.10 Search & Filters", table_cell_bold),
            Paragraph("Instant search by keyword/title, dynamic filtering by course, date range, or event classification.", table_cell),
            Paragraph("SearchTextField with DocumentListener, FilterToolBar", table_cell),
            Paragraph("Java Stream.filter() with Predicate lambdas", table_cell)
        ]
    ]
    t_feat = Table(feat_data, colWidths=[90, 160, 140, 150], repeatRows=1)
    t_feat.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.8),
    ]))
    story.append(t_feat)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 5: End-to-End User Workflows & Flowcharts
    # ------------------------------------------------------------------
    story.append(Paragraph("5. END-TO-END USER WORKFLOWS & PROCESS FLOWCHARTS", sec_heading))
    story.append(Paragraph(
        "Each stakeholder experiences a tailored workflow. Below is the operational lifecycle for scheduling and synchronizing academic events across the platform:",
        body
    ))
    story.append(create_event_lifecycle_diagram_bw())
    story.append(Spacer(1, 6))
    
    wf_data = [
        [Paragraph("Stakeholder", table_head), Paragraph("Step-by-Step Operational Workflow", table_head), Paragraph("Key Decisions & Validations", table_head)],
        [
            Paragraph("Student Workflow", table_cell_bold),
            Paragraph("1. Authenticates or selects demonstration profile.<br/>2. Lands on Monthly Calendar with enrolled course markers.<br/>3. Clicks date cell to open day schedule (lectures, tests, notes).<br/>4. Downloads teacher's uploaded PDF slides for the selected date.<br/>5. Adds personal study task in To-Do list with priority tag.<br/>6. Receives in-app pop-up reminder for tomorrow's assignment.<br/>7. Checks Semester Overview for syllabus completion % vs remaining days.", table_cell),
            Paragraph("• Cannot edit teacher's syllabus status.<br/>• Personal tasks remain completely private.<br/>• Cannot modify official university holiday entries.", table_cell)
        ],
        [
            Paragraph("Teacher Workflow", table_cell_bold),
            Paragraph("1. Logs in and selects assigned course from course switcher.<br/>2. Selects calendar date and clicks 'Add Academic Event'.<br/>3. Configures class test / assignment deadline and attaches PDF worksheet.<br/>4. Updates course syllabus list: checks off 'Inheritance & Polymorphism'.<br/>5. Triggers optional student notification for rescheduled quiz.<br/>6. Reviews course events timeline for upcoming midterm preparation.", table_cell),
            Paragraph("• Restricted to courses assigned to teacher_id.<br/>• File upload validated for size (&lt; 20MB) and extension.<br/>• Syllabus progress auto-recalculates upon save.", table_cell)
        ],
        [
            Paragraph("Administrator Workflow", table_cell_bold),
            Paragraph("1. Logs into Administrative console.<br/>2. Defines semester boundaries (start date, end date, examination weeks).<br/>3. Publishes university-wide holidays and convocation dates.<br/>4. Provisions course offerings, assigns faculty, and enrols students.<br/>5. Audits calendar data integrity across departments.", table_cell),
            Paragraph("• Enforces unique course codes & semester dates.<br/>• Official events broadcast to ALL calendars.<br/>• Cannot be overridden by students or teachers.", table_cell)
        ]
    ]
    t_wf = Table(wf_data, colWidths=[100, 290, 150], repeatRows=1)
    t_wf.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3.5),
    ]))
    story.append(t_wf)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 6: OODP & Advanced Java Syllabus Concept Mapping
    # ------------------------------------------------------------------
    story.append(Paragraph("6. REQUIRED JAVA & OODP CONCEPT MAPPING", sec_heading))
    story.append(Paragraph(
        "UniTrack is intentionally architected to demonstrate academic mastery of the required Java and Object-Oriented Design curriculum. Concepts are not superficially inserted; each addresses a genuine architectural requirement.",
        body
    ))
    
    oodp_data = [
        [Paragraph("Java / OODP Syllabus Topic", table_head), Paragraph("Concrete UniTrack Implementation", table_head), Paragraph("Key Java Classes / Interfaces", table_head), Paragraph("Software Engineering Justification", table_head)],
        [
            Paragraph("Inheritance & Abstraction", table_cell_bold),
            Paragraph("Abstract base <font name='Courier'>User</font> extended by <font name='Courier'>Student</font>, <font name='Courier'>Teacher</font>, and <font name='Courier'>Admin</font>. Common credentials and role definitions inherited.", table_cell),
            Paragraph("<font name='Courier'>User.java (abstract)<br/>Student.java<br/>Teacher.java<br/>Admin.java</font>", table_cell_code),
            Paragraph("Eliminates code duplication; enforces polymorphic role resolution across security boundaries.", table_cell)
        ],
        [
            Paragraph("Polymorphism & Interfaces", table_cell_bold),
            Paragraph("Polymorphic permission checks via <font name='Courier'>hasPermission(Action)</font>; domain contracts such as <font name='Courier'>Notifiable</font> and <font name='Courier'>Downloadable</font>.", table_cell),
            Paragraph("<font name='Courier'>Notifiable.java<br/>Downloadable.java<br/>Searchable.java</font>", table_cell_code),
            Paragraph("Enables open-closed principle; reminder engine handles any Notifiable entity seamlessly.", table_cell)
        ],
        [
            Paragraph("Collections Framework", table_cell_bold),
            Paragraph("• <font name='Courier'>List&lt;CalendarEvent&gt;</font> for chronological event sequences.<br/>• <font name='Courier'>Set&lt;Integer&gt;</font> for unique student enrollment IDs.<br/>• <font name='Courier'>Map&lt;LocalDate, List&lt;CalendarEvent&gt;&gt;</font> for instant O(1) day-cell event lookups.<br/>• <font name='Courier'>Queue&lt;Notification&gt;</font> for alert dispatching.", table_cell),
            Paragraph("<font name='Courier'>ArrayList, HashSet,<br/>HashMap, TreeMap,<br/>ConcurrentHashMap</font>", table_cell_code),
            Paragraph("Demonstrates appropriate collection selection based on time complexity and data semantics.", table_cell)
        ],
        [
            Paragraph("Java 8 Streams & Lambdas", table_cell_bold),
            Paragraph("Filtering upcoming tests by course, sorting events by start time, and aggregating completed syllabus topics.", table_cell),
            Paragraph("<font name='Courier'>events.stream()<br/> .filter(...)<br/> .sorted(...)<br/> .collect(...)</font>", table_cell_code),
            Paragraph("Declarative, readable data transformations without clumsy nested for-loops.", table_cell)
        ],
        [
            Paragraph("Generics & Generic DAO", table_cell_bold),
            Paragraph("Reusable Generic Repository defining standard CRUD operations parameterised by entity type <font name='Courier'>T</font> and ID type <font name='Courier'>ID</font>.", table_cell),
            Paragraph("<font name='Courier'>Repository&lt;T, ID&gt;<br/>AbstractJdbcRepository&lt;T, ID&gt;</font>", table_cell_code),
            Paragraph("Type safety at compile time; avoids runtime casting and boilerplate DAO code.", table_cell)
        ],
        [
            Paragraph("Multithreading & Concurrency", table_cell_bold),
            Paragraph("Background daemon checking deadline thresholds every 60s; thread-safe event caching; clean JVM shutdown hooks.", table_cell),
            Paragraph("<font name='Courier'>ScheduledExecutorService<br/>ReentrantLock<br/>AtomicBoolean</font>", table_cell_code),
            Paragraph("Prevents UI freezing; demonstrates thread safety, deadlock prevention, and graceful shutdown.", table_cell)
        ],
        [
            Paragraph("Java Networking", table_cell_bold),
            Paragraph("Client-server socket module enabling UniTrack client to fetch university-wide calendar feeds or academic holiday data.", table_cell),
            Paragraph("<font name='Courier'>ServerSocket<br/>Socket<br/>InputStream / OutputStream</font>", table_cell_code),
            Paragraph("Demonstrates reliable TCP socket communication, timeout handling, and stream parsing.", table_cell)
        ],
        [
            Paragraph("JDBC & Relational DB", table_cell_bold),
            Paragraph("Connecting to MySQL via JDBC driver, executing parameterized queries, transaction control (<font name='Courier'>commit/rollback</font>).", table_cell),
            Paragraph("<font name='Courier'>Connection, PreparedStatement<br/>ResultSet, Savepoint</font>", table_cell_code),
            Paragraph("Prevents SQL injection vulnerabilities; ensures atomicity on multi-table updates.", table_cell)
        ],
        [
            Paragraph("GUI / Java Swing (EDT)", table_cell_bold),
            Paragraph("Interactive desktop client with custom painted calendar grids, forms, and dialogs. Background tasks offloaded from EDT.", table_cell),
            Paragraph("<font name='Courier'>SwingWorker&lt;T, V&gt;<br/>SwingUtilities.invokeLater<br/>JPanel, JTable, JDialog</font>", table_cell_code),
            Paragraph("Maintains a responsive 60fps UI while performing heavy disk and network I/O.", table_cell)
        ],
        [
            Paragraph("Servlets / JSP (Optional)", table_cell_bold),
            Paragraph("Standalone web module sharing the exact same Service and DAO layers to render calendar events in a web browser.", table_cell),
            Paragraph("<font name='Courier'>CalendarServlet.java<br/>calendarView.jsp<br/>HttpSession</font>", table_cell_code),
            Paragraph("Validates enterprise multi-channel architecture sharing common business logic.", table_cell)
        ]
    ]
    t_oodp = Table(oodp_data, colWidths=[105, 175, 120, 140], repeatRows=1)
    t_oodp.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.8),
    ]))
    story.append(t_oodp)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 7: Proposed Class Design & Domain Hierarchy
    # ------------------------------------------------------------------
    story.append(Paragraph("7. PROPOSED CLASS DESIGN & DOMAIN MODEL", sec_heading))
    story.append(Paragraph(
        "The class structure strictly follows the Single Responsibility Principle. Below is the taxonomy of core classes across architectural layers:",
        body
    ))
    
    class_data = [
        [Paragraph("Layer", table_head), Paragraph("Class / Interface", table_head), Paragraph("Key Attributes & Fields", table_head), Paragraph("Key Methods & Responsibilities", table_head)],
        [
            Paragraph("Model (User)", table_cell_bold),
            Paragraph("<font name='Courier'>User (abstract)<br/>Student, Teacher, Admin</font>", table_cell_code),
            Paragraph("<font name='Courier'>int id; String name; String email;<br/>String passwordHash; Role role;</font>", table_cell),
            Paragraph("<font name='Courier'>hasPermission(Action a);<br/>getDashboardView(); getEnrolledCourses();</font>", table_cell)
        ],
        [
            Paragraph("Model (Core)", table_cell_bold),
            Paragraph("<font name='Courier'>CalendarEvent</font>", table_cell_code),
            Paragraph("<font name='Courier'>int id; String title; EventType type;<br/>LocalDateTime start, end; Course course;<br/>User creator; String location; String filePath;</font>", table_cell),
            Paragraph("<font name='Courier'>isUpcoming(); isOverdue();<br/>conflictsWith(CalendarEvent other);<br/>hasAttachment();</font>", table_cell)
        ],
        [
            Paragraph("Model (Academic)", table_cell_bold),
            Paragraph("<font name='Courier'>Course<br/>Semester<br/>SyllabusTopic<br/>TodoItem</font>", table_cell_code),
            Paragraph("<font name='Courier'>code, title, teacherId;<br/>startDate, endDate;<br/>topicName, isCompleted, order;<br/>ownerId, title, priority, isDone;</font>", table_cell),
            Paragraph("<font name='Courier'>calculateProgress(); markComplete();<br/>isExpired(); getRemainingDays();<br/>sortByPriority();</font>", table_cell)
        ],
        [
            Paragraph("Service Layer", table_cell_bold),
            Paragraph("<font name='Courier'>CalendarService<br/>CourseService<br/>NotificationService<br/>FileService</font>", table_cell_code),
            Paragraph("Repository dependencies, ScheduledExecutor, upload directory path configuration.", table_cell),
            Paragraph("<font name='Courier'>createEvent(User u, Event e);<br/>getEventsByDate(LocalDate d);<br/>pollApproachingDeadlines();<br/>saveAttachment(File f);</font>", table_cell)
        ],
        [
            Paragraph("Persistence", table_cell_bold),
            Paragraph("<font name='Courier'>Repository&lt;T, ID&gt;<br/>EventDao, UserDao,<br/>CourseDao, TaskDao</font>", table_cell_code),
            Paragraph("Database connection credentials, SQL PreparedStatement constants, ResultSet mappers.", table_cell),
            Paragraph("<font name='Courier'>findById(ID id); findAll();<br/>save(T entity); update(T entity);<br/>deleteById(ID id); findByDateRange(...);</font>", table_cell)
        ],
        [
            Paragraph("Utilities", table_cell_bold),
            Paragraph("<font name='Courier'>DatabaseConnectionPool<br/>DateUtils, SecurityUtils</font>", table_cell_code),
            Paragraph("JDBC DataSource, BCrypt hasher, standard DateTimeFormatter patterns.", table_cell),
            Paragraph("<font name='Courier'>getConnection(); hashPassword(pwd);<br/>verifyPassword(pwd, hash); formatRange(...);</font>", table_cell)
        ]
    ]
    t_class = Table(class_data, colWidths=[75, 125, 160, 180], repeatRows=1)
    t_class.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_class)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 8: Database Architecture & Data Dictionary
    # ------------------------------------------------------------------
    story.append(Paragraph("8. DATABASE ARCHITECTURE & DATA DICTIONARY", sec_heading))
    story.append(Paragraph(
        "The relational schema is fully normalized (3NF) to eliminate data redundancy while maintaining referential integrity across MySQL tables via foreign keys and cascade rules.",
        body
    ))
    
    db_data = [
        [Paragraph("Table Name", table_head), Paragraph("Primary Key", table_head), Paragraph("Foreign Keys", table_head), Paragraph("Key Columns & Types", table_head), Paragraph("Description & Constraints", table_head)],
        [
            Paragraph("USERS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("None", table_cell),
            Paragraph("<font name='Courier'>name VARCHAR(100), email VARCHAR(120),<br/>password_hash VARCHAR(255), role VARCHAR(20)</font>", table_cell_code),
            Paragraph("System users. <font name='Courier'>email</font> is UNIQUE. <font name='Courier'>role</font> ENUM('STUDENT','TEACHER','ADMIN').", table_cell)
        ],
        [
            Paragraph("SEMESTERS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("None", table_cell),
            Paragraph("<font name='Courier'>name VARCHAR(60), start_date DATE,<br/>end_date DATE, is_active BOOLEAN</font>", table_cell_code),
            Paragraph("Academic terms. Constraint: <font name='Courier'>CHECK (end_date &gt; start_date)</font>.", table_cell)
        ],
        [
            Paragraph("COURSES", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>semester_id -&gt; SEMESTERS<br/>teacher_id -&gt; USERS</font>", table_cell_code),
            Paragraph("<font name='Courier'>course_code VARCHAR(20), title VARCHAR(120),<br/>credits INT, semester_id INT, teacher_id INT</font>", table_cell_code),
            Paragraph("Academic courses. <font name='Courier'>course_code</font> is UNIQUE within a semester.", table_cell)
        ],
        [
            Paragraph("ENROLLMENTS", table_cell_bold),
            Paragraph("<font name='Courier'>student_id + course_id</font>", table_cell_code),
            Paragraph("<font name='Courier'>student_id -&gt; USERS<br/>course_id -&gt; COURSES</font>", table_cell_code),
            Paragraph("<font name='Courier'>student_id INT, course_id INT,<br/>enrolled_at TIMESTAMP DEFAULT NOW()</font>", table_cell_code),
            Paragraph("Composite PK. Enforces that only users with <font name='Courier'>STUDENT</font> role can be enrolled.", table_cell)
        ],
        [
            Paragraph("CALENDAR_EVENTS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>course_id -&gt; COURSES<br/>created_by -&gt; USERS</font>", table_cell_code),
            Paragraph("<font name='Courier'>title VARCHAR(150), description TEXT,<br/>event_type VARCHAR(30), start_dt DATETIME,<br/>end_dt DATETIME, location VARCHAR(100)</font>", table_cell_code),
            Paragraph("Universal calendar entries. Indexed on <font name='Courier'>(start_dt, end_dt)</font> for rapid month querying.", table_cell)
        ],
        [
            Paragraph("NOTES", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>course_id -&gt; COURSES<br/>event_id -&gt; EVENTS<br/>uploaded_by -&gt; USERS</font>", table_cell_code),
            Paragraph("<font name='Courier'>title VARCHAR(120), file_path VARCHAR(255),<br/>file_size BIGINT, mime_type VARCHAR(50),<br/>uploaded_at TIMESTAMP</font>", table_cell_code),
            Paragraph("Stores file metadata. Physical file lives in configured local directory; DB stores relative path.", table_cell)
        ],
        [
            Paragraph("ASSESSMENTS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>event_id (UNIQUE) -&gt;<br/>CALENDAR_EVENTS</font>", table_cell_code),
            Paragraph("<font name='Courier'>assessment_type VARCHAR(30), instructions TEXT,<br/>total_marks INT, weightage DECIMAL(5,2)</font>", table_cell_code),
            Paragraph("Extended assessment metadata. 1-to-1 link with CALENDAR_EVENTS (prevents event duplication).", table_cell)
        ],
        [
            Paragraph("SYLLABUS_TOPICS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>course_id -&gt; COURSES<br/>completed_by -&gt; USERS</font>", table_cell_code),
            Paragraph("<font name='Courier'>course_id INT, title VARCHAR(150),<br/>topic_order INT, is_completed BOOLEAN,<br/>completed_at DATETIME</font>", table_cell_code),
            Paragraph("Ordered curriculum topics. Teachers check off topics to auto-calculate progress %.", table_cell)
        ],
        [
            Paragraph("TODO_ITEMS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>owner_id -&gt; USERS</font>", table_cell_code),
            Paragraph("<font name='Courier'>owner_id INT, title VARCHAR(150),<br/>due_dt DATETIME, priority VARCHAR(10),<br/>is_completed BOOLEAN</font>", table_cell_code),
            Paragraph("Personal student tasks. Strictly private to the owning user. Priority: HIGH, MED, LOW.", table_cell)
        ],
        [
            Paragraph("NOTIFICATIONS", table_cell_bold),
            Paragraph("<font name='Courier'>id (INT PK AI)</font>", table_cell_code),
            Paragraph("<font name='Courier'>recipient_id -&gt; USERS<br/>event_id -&gt; EVENTS</font>", table_cell_code),
            Paragraph("<font name='Courier'>message VARCHAR(255), scheduled_at DATETIME,<br/>sent_at DATETIME, status VARCHAR(20),<br/>dedup_key VARCHAR(100) UNIQUE</font>", table_cell_code),
            Paragraph("Alert records. <font name='Courier'>dedup_key</font> prevents sending duplicate alerts for the same event window.", table_cell)
        ]
    ]
    t_db = Table(db_data, colWidths=[95, 80, 100, 130, 135], repeatRows=1)
    t_db.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.5),
    ]))
    story.append(t_db)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 9: Advanced Technical Subsystems
    # ------------------------------------------------------------------
    story.append(Paragraph("9. ADVANCED TECHNICAL SUBSYSTEMS", sec_heading))
    story.append(Paragraph(
        "To fulfill high-scoring academic requirements, UniTrack incorporates two dedicated subsystems: a background multithreaded notification engine and a client-server socket synchronization demonstration.",
        body
    ))
    
    story.append(Paragraph("9.1 Multithreaded Background Reminder Engine", subsec_heading))
    story.append(Paragraph(
        "The reminder subsystem runs autonomously as a daemon thread. It evaluates upcoming deadlines, verifies deduplication hashes, and uses <font name='Courier'>SwingUtilities.invokeLater</font> to safely pass notifications to the Swing Event Dispatch Thread.",
        body
    ))
    story.append(create_concurrency_diagram_bw())
    story.append(Spacer(1, 6))
    
    story.append(Paragraph("9.2 Client-Server Socket Integration", subsec_heading))
    story.append(Paragraph(
        "To meet the Java Networking requirement without relying on external university APIs, UniTrack includes a lightweight academic server daemon. The client socket connects, requests campus-wide holiday feeds, and parses incoming streams into calendar events.",
        body
    ))
    story.append(create_networking_diagram_bw())
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 10: 10-Phase Implementation Roadmap
    # ------------------------------------------------------------------
    story.append(Paragraph("10. 10-PHASE IMPLEMENTATION ROADMAP & SPRINT PIPELINE", sec_heading))
    story.append(Paragraph(
        "Development is sequenced across 10 disciplined phases grouped into 5 iterative sprints, guaranteeing an operational MVP at early stages while methodically expanding into advanced concurrency, networking, and optional web modules.",
        body
    ))
    story.append(create_pipeline_diagram_bw())
    story.append(Spacer(1, 6))
    
    phases_data = [
        [Paragraph("Phase", table_head), Paragraph("Focus & Objective", table_head), Paragraph("Key Deliverables & Action Items", table_head), Paragraph("OODP Concept", table_head), Paragraph("Milestone", table_head)],
        [
            Paragraph("Phase 1", table_cell_bold),
            Paragraph("Requirements & UI Blueprint", table_cell),
            Paragraph("Finalize UI reference from student design; define user stories, use cases, and initial UML class diagrams.", table_cell),
            Paragraph("OO Analysis & Design", table_cell),
            Paragraph("Milestone 1<br/>(Spec Complete)", table_cell_bold)
        ],
        [
            Paragraph("Phase 2", table_cell_bold),
            Paragraph("Project Setup & Domain Models", table_cell),
            Paragraph("Establish package hierarchy; build POJO entities (<font name='Courier'>User</font>, <font name='Courier'>CalendarEvent</font>, <font name='Courier'>Course</font>) and enums.", table_cell),
            Paragraph("Encapsulation, Enums", table_cell),
            Paragraph("Milestone 2<br/>(Domain Ready)", table_cell_bold)
        ],
        [
            Paragraph("Phase 3", table_cell_bold),
            Paragraph("Calendar Core (Swing GUI)", table_cell),
            Paragraph("Build 7x6 month grid panel; implement previous/next/today navigation; bind date selection to detail pane.", table_cell),
            Paragraph("Swing GUI, Event Dispatch", table_cell),
            Paragraph("Milestone 3<br/>(UI Prototype)", table_cell_bold)
        ],
        [
            Paragraph("Phase 4", table_cell_bold),
            Paragraph("Database Persistence (JDBC)", table_cell),
            Paragraph("Design MySQL schema; establish ConnectionPool; write generic <font name='Courier'>Repository&lt;T, ID&gt;</font> and EventDAO with PreparedStatement.", table_cell),
            Paragraph("Generics, JDBC, SQL", table_cell),
            Paragraph("Milestone 4<br/>(Core MVP)", table_cell_bold)
        ],
        [
            Paragraph("Phase 5", table_cell_bold),
            Paragraph("RBAC & Course Management", table_cell),
            Paragraph("Implement abstract User hierarchy; code Student, Teacher, Admin roles; build course enrollment authorization logic.", table_cell),
            Paragraph("Inheritance, Polymorphism", table_cell),
            Paragraph("Milestone 5<br/>(RBAC Live)", table_cell_bold)
        ],
        [
            Paragraph("Phase 6", table_cell_bold),
            Paragraph("Academic Features", table_cell),
            Paragraph("Integrate PDF attachment uploads/downloads; syllabus topic progress tracking; student private to-do list.", table_cell),
            Paragraph("Java NIO, Stream API", table_cell),
            Paragraph("Milestone 6<br/>(Full Academic)", table_cell_bold)
        ],
        [
            Paragraph("Phase 7", table_cell_bold),
            Paragraph("Concurrency & Notifications", table_cell),
            Paragraph("Implement <font name='Courier'>ScheduledExecutorService</font> daemon; build deduplicated reminder checking and EDT notification dialogs.", table_cell),
            Paragraph("Multithreading, Locks", table_cell),
            Paragraph("Milestone 7<br/>(Alert Engine)", table_cell_bold)
        ],
        [
            Paragraph("Phase 8", table_cell_bold),
            Paragraph("Java Networking Module", table_cell),
            Paragraph("Build client-server TCP socket demonstration for synchronized holiday and university event fetching.", table_cell),
            Paragraph("Sockets, I/O Streams", table_cell),
            Paragraph("Milestone 8<br/>(Network Ready)", table_cell_bold)
        ],
        [
            Paragraph("Phase 9", table_cell_bold),
            Paragraph("Web Module (Servlets/JSP)", table_cell),
            Paragraph("If mandated by instructor: create small companion web app sharing the same Service and DAO layers.", table_cell),
            Paragraph("Java Servlets, JSP, HTTP", table_cell),
            Paragraph("Milestone 9<br/>(Web Hybrid)", table_cell_bold)
        ],
        [
            Paragraph("Phase 10", table_cell_bold),
            Paragraph("Testing, QA & Packaging", table_cell),
            Paragraph("Execute boundary tests, SQL injection tests, thread safety audits; compile final UML diagrams, slides, and project report.", table_cell),
            Paragraph("Software QA & Delivery", table_cell),
            Paragraph("Milestone 10<br/>(Final Submission)", table_cell_bold)
        ]
    ]
    t_phase = Table(phases_data, colWidths=[65, 110, 185, 95, 85], repeatRows=1)
    t_phase.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.8),
    ]))
    story.append(t_phase)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 11: Scope Management & MoSCoW Prioritization
    # ------------------------------------------------------------------
    story.append(Paragraph("11. SCOPE MANAGEMENT & MOSCOW PRIORITIZATION", sec_heading))
    story.append(Paragraph(
        "To ensure successful completion before academic deadlines, requirements are categorized using the MoSCoW framework. The team guarantees all <b>Must-Have (MVP)</b> deliverables before activating secondary enhancements.",
        body
    ))
    
    moscow_data = [
        [Paragraph("Priority Level", table_head), Paragraph("Features & Scope Inclusions", table_head), Paragraph("Technical Justification & Risk Mitigation", table_head)],
        [
            Paragraph("MUST HAVE<br/>(Core MVP)", table_cell_bold),
            Paragraph("• Interactive Monthly Calendar Grid with Day Navigation<br/>• Create, Read, Update, Delete (CRUD) for Calendar Events<br/>• MySQL JDBC persistence with PreparedStatement<br/>• Abstract User base with Student & Teacher role separation<br/>• Course association and Event Type filtering<br/>• Personal To-Do task checklist with persistence", table_cell),
            Paragraph("Essential core without which the project cannot be demonstrated. Fulfills the minimum OODP passing criteria.", table_cell)
        ],
        [
            Paragraph("SHOULD HAVE<br/>(High Priority)", table_cell_bold),
            Paragraph("• Notes and PDF attachment upload/download system<br/>• Teacher-managed syllabus topic list with % calculation<br/>• Multithreaded background reminder engine (ScheduledExecutor)<br/>• TCP Socket / URL academic holiday sync demonstration<br/>• Stream API sorting, filtering, and metric calculation", table_cell),
            Paragraph("Fulfills advanced course requirements (Multithreading, Networking, Collections, Streams) for maximum grading marks.", table_cell)
        ],
        [
            Paragraph("COULD HAVE<br/>(Nice to Have)", table_cell_bold),
            Paragraph("• Week View and Day View calendar representations<br/>• Administrator console for global holiday scheduling<br/>• Companion Servlet/JSP browser view (if requested by instructor)<br/>• Dark/Light visual theme switcher in Swing", table_cell),
            Paragraph("Added only after the core desktop workflow and database operations are 100% stable and verified.", table_cell)
        ],
        [
            Paragraph("WON'T HAVE<br/>(Out of Scope)", table_cell_bold),
            Paragraph("• Bi-directional live sync with Google Calendar API / Outlook<br/>• Real-time automated assignment grading / code evaluation<br/>• Online payment or university fee processing gateways", table_cell),
            Paragraph("Prevents scope creep; keeps project focused on OODP principles rather than external third-party cloud infrastructure.", table_cell)
        ]
    ]
    t_moscow = Table(moscow_data, colWidths=[100, 240, 200], repeatRows=1)
    t_moscow.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3.5),
    ]))
    story.append(t_moscow)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 12: Quality Assurance & Testing Checklist
    # ------------------------------------------------------------------
    story.append(Paragraph("12. QUALITY ASSURANCE & TESTING CHECKLIST", sec_heading))
    story.append(Paragraph(
        "A rigorous testing plan validates edge cases, boundary conditions, thread safety, and security vulnerabilities across all functional units:",
        body
    ))
    
    test_data = [
        [Paragraph("Subsystem", table_head), Paragraph("Test Scenario & Test Case", table_head), Paragraph("Input / Condition", table_head), Paragraph("Expected System Outcome", table_head)],
        [
            Paragraph("Calendar Navigation", table_cell_bold),
            Paragraph("Year boundary rollover and leap year handling", table_cell),
            Paragraph("Navigate December 2026 -&gt; January 2027; Feb 2028", table_cell),
            Paragraph("Correct month name, 31 days in Jan, 29 days in leap Feb, correct weekday alignment.", table_cell)
        ],
        [
            Paragraph("RBAC Security", table_cell_bold),
            Paragraph("Student attempts unauthorized syllabus topic modification", table_cell),
            Paragraph("Student role sends updateTopicCompletion() request", table_cell),
            Paragraph("Service throws SecurityException; UI displays access-denied error dialog.", table_cell)
        ],
        [
            Paragraph("Data Integrity", table_cell_bold),
            Paragraph("SQL Injection prevention on search and input fields", table_cell),
            Paragraph("Enter \"' OR '1'='1\" in search box or event title", table_cell),
            Paragraph("PreparedStatement treats input as literal text; no syntax error or unauthorized data leak.", table_cell)
        ],
        [
            Paragraph("File Handling", table_cell_bold),
            Paragraph("Missing file or corrupted attachment download", table_cell),
            Paragraph("Delete physical file from disk; click Download in UI", table_cell),
            Paragraph("FileService detects missing file gracefully; logs warning; alerts user without crashing.", table_cell)
        ],
        [
            Paragraph("Concurrency", table_cell_bold),
            Paragraph("Background reminder daemon during application exit", table_cell),
            Paragraph("User closes main window while executor thread is active", table_cell),
            Paragraph("WindowListener calls executor.shutdownNow(); background thread terminates immediately.", table_cell)
        ],
        [
            Paragraph("Networking", table_cell_bold),
            Paragraph("Calendar synchronization server unavailable", table_cell),
            Paragraph("Attempt sync when ServerSocket daemon is offline", table_cell),
            Paragraph("SocketTimeoutException caught; informs user offline mode active; calendar stays usable.", table_cell)
        ]
    ]
    t_test = Table(test_data, colWidths=[95, 155, 140, 150], repeatRows=1)
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.8),
    ]))
    story.append(t_test)
    story.append(Spacer(1, 8))
    
    # ------------------------------------------------------------------
    # Section 13: Project Deliverables & Submission Packaging
    # ------------------------------------------------------------------
    story.append(Paragraph("13. FINAL DELIVERABLES & SUBMISSION PACKAGING", sec_heading))
    story.append(Paragraph(
        "Final academic submission package is organized strictly according to standard Java project conventions:",
        body
    ))
    
    deliv_data = [
        [Paragraph("Deliverable Item", table_head), Paragraph("Format / Artifact", table_head), Paragraph("Contents & Description", table_head)],
        [
            Paragraph("Complete Source Code", table_cell_bold),
            Paragraph("Clean Java Project (.zip)", table_cell),
            Paragraph("Fully commented, production-grade Java source code organized into layered packages.", table_cell)
        ],
        [
            Paragraph("Database DDL & Demo Data", table_cell_bold),
            Paragraph("schema.sql + sample_data.sql", table_cell),
            Paragraph("DDL script creating all 10 tables, indexes, constraints, and populated demo datasets.", table_cell)
        ],
        [
            Paragraph("UML Architecture Diagrams", table_cell_bold),
            Paragraph("PDF / High-Res PNG", table_cell),
            Paragraph("Complete Use-Case Diagram, Class Hierarchy Diagram, and Relational Entity-Relationship Diagram.", table_cell)
        ],
        [
            Paragraph("Setup & Execution Guide", table_cell_bold),
            Paragraph("README.md", table_cell),
            Paragraph("Prerequisites (JDK, MySQL), DB setup commands, compile instructions, and login credentials.", table_cell)
        ],
        [
            Paragraph("OODP Concept Mapping Document", table_cell_bold),
            Paragraph("Academic Report Section", table_cell),
            Paragraph("Detailed written justification explaining how each required OOP concept was practically utilized.", table_cell)
        ],
        [
            Paragraph("Presentation Slides & Script", table_cell_bold),
            Paragraph("PowerPoint / PDF + Demo Script", table_cell),
            Paragraph("10-slide deck summarizing problem, architecture, OODP demonstration, and live demo script.", table_cell)
        ]
    ]
    t_deliv = Table(deliv_data, colWidths=[130, 120, 290], repeatRows=1)
    t_deliv.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), COLOR_BLACK),
        ('GRID', (0, 0), (-1, -1), 0.5, COLOR_BORDER),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, COLOR_BG_ALT]),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ]))
    story.append(t_deliv)
    story.append(Spacer(1, 6))
    
    # Recommended Package Tree (Strict Black & White)
    story.append(Paragraph("Recommended Package Structure:", subsec_heading))
    pkg_tree = """<b>unitrack/</b><br/>
&nbsp;&nbsp;[+] <b>model/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Domain POJOs: User, Student, Teacher, Admin, CalendarEvent, Course, Semester, SyllabusTopic, TodoItem, Notification<br/>
&nbsp;&nbsp;[+] <b>view/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Swing GUI: MainCalendarFrame, MonthGridPanel, DayDetailDialog, EventFormDialog, CourseView, TodoPanel<br/>
&nbsp;&nbsp;[+] <b>controller/</b> &nbsp;&nbsp;&nbsp;Event Controllers: CalendarController, AuthController, CourseController, TaskController<br/>
&nbsp;&nbsp;[+] <b>service/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Business Logic: CalendarService, AuthService, CourseService, NotificationService, FileService, ProgressService<br/>
&nbsp;&nbsp;[+] <b>repository/</b> &nbsp;&nbsp;&nbsp;Data Persistence: Repository&lt;T, ID&gt;, EventDao, UserDao, CourseDao, TaskDao, NoteDao<br/>
&nbsp;&nbsp;[+] <b>database/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Infrastructure: ConnectionPool, DatabaseConfig, MigrationRunner<br/>
&nbsp;&nbsp;[+] <b>concurrency/</b> &nbsp;&nbsp;Threads: ReminderDaemon, ThreadFactoryHelper<br/>
&nbsp;&nbsp;[+] <b>networking/</b> &nbsp;&nbsp;&nbsp;Sockets: CalendarSyncClient, AcademicServerDaemon<br/>
&nbsp;&nbsp;[+] <b>util/</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Helpers: DateUtils, SecurityUtils, InputValidator, Constants"""
    
    pkg_table = Table([[Paragraph(pkg_tree, table_cell_code)]], colWidths=[540])
    pkg_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), COLOR_BG_ALT),
        ('BOX', (0, 0), (-1, -1), 0.75, COLOR_BORDER),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(pkg_table)
    
    # Build Document
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"B&W Polished Project Plan PDF successfully generated at: {output_path}")

if __name__ == '__main__':
    target = os.path.abspath('UniTrack_Smart_Academic_Calendar_OODP_Project_Plan.pdf')
    generate_pdf(target)
