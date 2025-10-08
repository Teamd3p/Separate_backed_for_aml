# Tesseract OCR Installation Guide

## Windows Installation

### Method 1: Using Installer (Recommended)
1. Download Tesseract installer from: https://github.com/UB-Mannheim/tesseract/wiki
2. Run the installer and install to default location: `C:\Program Files\Tesseract-OCR`
3. Add to system PATH: `C:\Program Files\Tesseract-OCR`
4. Update `application-ocr.properties`:
   ```properties
   tesseract.datapath=C:\\Program Files\\Tesseract-OCR\\tessdata
   ```

### Method 2: Using Chocolatey
```bash
choco install tesseract
```

## Linux Installation

### Ubuntu/Debian
```bash
sudo apt update
sudo apt install tesseract-ocr tesseract-ocr-eng
```

### CentOS/RHEL
```bash
sudo yum install epel-release
sudo yum install tesseract tesseract-langpack-eng
```

## macOS Installation

### Using Homebrew
```bash
brew install tesseract
```

## Verification

Test installation:
```bash
tesseract --version
```

## Configuration

Update your `application.properties`:
```properties
# Switch to real OCR
ocr.service.type=tesseract

# Tesseract paths (adjust based on your installation)
# Windows
tesseract.datapath=C:\\Program Files\\Tesseract-OCR\\tessdata
# Linux
tesseract.datapath=/usr/share/tesseract-ocr/4.00/tessdata
# macOS
tesseract.datapath=/usr/local/share/tessdata
```

## Language Packs

For additional languages:
```bash
# Ubuntu/Debian
sudo apt install tesseract-ocr-hin tesseract-ocr-tam

# Windows - download from GitHub releases
# Place .traineddata files in tessdata folder
```

## Troubleshooting

### Common Issues:
1. **Path not found**: Verify tesseract installation path
2. **Permission denied**: Ensure read access to tessdata folder
3. **Language not found**: Install required language packs
4. **Poor OCR quality**: Ensure high-resolution images (300 DPI minimum)

### Performance Tips:
- Use PDF files when possible (better OCR results)
- Ensure good image quality (clear, high contrast)
- Preprocess images (deskew, denoise) for better results
