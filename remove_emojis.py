#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Emoji kaldırma script'i
Java dosyalarındaki emojileri kaldırır
"""

import os
import re
import glob

def remove_emojis_from_file(file_path):
    """Dosyadan emojileri kaldırır"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Emoji patterns to remove
        emoji_patterns = [
            r'✅\s*',  # Check mark
            r'❌\s*',  # Cross mark
            r'⚠️\s*',  # Warning
            r'ℹ️\s*',  # Info
            r'🔍\s*',  # Magnifying glass
            r'📈\s*',  # Chart
            r'📊\s*',  # Bar chart
            r'📅\s*',  # Calendar
            r'🎯\s*',  # Target
            r'📍\s*',  # Pin
            r'👥\s*',  # People
            r'🆕\s*',  # New
            r'💾\s*',  # Floppy disk
            r'📂\s*',  # Folder
            r'🗑️\s*',  # Trash
            r'✏️\s*',  # Pencil
            r'⚡\s*',  # Lightning
            r'👤\s*',  # Person
            r'📧\s*',  # Email
            r'📞\s*',  # Phone
            r'🆔\s*',  # ID
            r'⏳\s*',  # Hourglass
            r'🎓\s*',  # Graduation cap
            r'📝\s*',  # Memo
            r'🔧\s*',  # Wrench
            r'⚙️\s*',  # Gear
            r'🚀\s*',  # Rocket
            r'💡\s*',  # Light bulb
            r'🎨\s*',  # Palette
            r'🔒\s*',  # Lock
            r'🔓\s*',  # Unlock
            r'📋\s*',  # Clipboard
            r'📄\s*',  # Document
            r'📑\s*',  # Bookmark tabs
            r'🔄\s*',  # Refresh
            r'⭐\s*',  # Star
            r'🏆\s*',  # Trophy
            r'🎉\s*',  # Party
            r'🎊\s*',  # Confetti
            r'💼\s*',  # Briefcase
            r'🏠\s*',  # House
            r'🌟\s*',  # Glowing star
            r'💻\s*',  # Laptop
            r'🖥️\s*',  # Desktop
            r'📱\s*',  # Mobile phone
            r'⌚\s*',  # Watch
            r'🔔\s*',  # Bell
            r'🔕\s*',  # Bell with slash
            r'📢\s*',  # Loudspeaker
            r'📣\s*',  # Megaphone
            r'🎵\s*',  # Musical note
            r'🎶\s*',  # Musical notes
            r'🎤\s*',  # Microphone
            r'🎧\s*',  # Headphones
            r'📻\s*',  # Radio
            r'📺\s*',  # Television
            r'📷\s*',  # Camera
            r'📹\s*',  # Video camera
            r'🎬\s*',  # Clapper board
            r'🎮\s*',  # Video game
            r'🕹️\s*',  # Joystick
            r'🎲\s*',  # Dice
            r'♠️\s*',  # Spade
            r'♥️\s*',  # Heart
            r'♦️\s*',  # Diamond
            r'♣️\s*',  # Club
            r'🃏\s*',  # Joker
            r'🀄\s*',  # Mahjong
            r'🎯\s*',  # Direct hit
            r'🎱\s*',  # Pool 8 ball
            r'🔮\s*',  # Crystal ball
            r'🎪\s*',  # Circus tent
            r'🎭\s*',  # Performing arts
            r'🖼️\s*',  # Framed picture
            r'🎨\s*',  # Artist palette
            r'🧵\s*',  # Thread
            r'🪡\s*',  # Sewing needle
            r'🧶\s*',  # Yarn
            r'🪢\s*',  # Knot
        ]
        
        # Remove emojis
        for pattern in emoji_patterns:
            content = re.sub(pattern, '', content)
        
        # Clean up multiple spaces
        content = re.sub(r'\s+', ' ', content)
        content = re.sub(r'\s*\n\s*', '\n', content)
        
        # Write back if changed
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Emojiler kaldırıldı: {file_path}")
            return True
        else:
            print(f"Emoji bulunamadı: {file_path}")
            return False
            
    except Exception as e:
        print(f"Hata {file_path}: {e}")
        return False

def main():
    """Ana fonksiyon"""
    # Java dosyalarını bul
    java_files = glob.glob("src/**/*.java", recursive=True)
    
    print(f"{len(java_files)} Java dosyası bulundu")
    
    changed_files = 0
    for java_file in java_files:
        if remove_emojis_from_file(java_file):
            changed_files += 1
    
    print(f"\nToplam {changed_files} dosya güncellendi")

if __name__ == "__main__":
    main()
