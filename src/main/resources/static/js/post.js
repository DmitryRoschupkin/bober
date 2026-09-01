document.addEventListener('click', e => {
    const editBtn = e.target.closest('.author-microblog__post-edit-btn');
    const commentBtn = e.target.closest('.comments-btn');

    if (editBtn) {
        const postBody = editBtn.closest('.author-microblog__post');
        if (postBody) {
            const editForm = postBody.querySelector(':scope > .author-microblog__post-edit-form');
            if (editForm) {
                const isOpen = !editForm.classList.contains('is-open');
                togglePostEditForm(editForm, isOpen);
            }
        }
    }

    if (commentBtn) {
        const postBody = commentBtn.closest('.author-microblog__post');
        if (postBody) {
            const commentsSection = postBody.querySelector('.post-comments-section')
                || (postBody.nextElementSibling?.classList.contains('post-comments-section')
                    ? postBody.nextElementSibling
                    : null);

            if (commentsSection) {
                const isOpen = !commentsSection.classList.contains('is-open');
                toggleCommentsSection(commentsSection, isOpen);
            }
        }
    }
});

function togglePostEditForm(editForm, show) {
    if (show) {
        editForm.classList.add('is-open');
        const input = editForm.querySelector('.author-microblog__post-edit-text');
        if (input) {
            input.focus();
            input.selectionStart = input.selectionEnd = input.value.length;
        }
    } else {
        editForm.classList.remove('is-open');
    }
}

function toggleCommentsSection(commentsSection, show) {
    if (show) {
        commentsSection.classList.add('is-open');
    } else {
        commentsSection.classList.remove('is-open');
    }
}

document.addEventListener("DOMContentLoaded", function () {
    if (window.location.hash) {
        const hash = window.location.hash;
        const target = document.querySelector(hash);

        if (target) {
            setTimeout(() => {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }, 150);
        }
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const fileInput = document.getElementById('photoFile');
    const fileNameText = document.getElementById('fileNameText');
    const defaultText = fileNameText.textContent;
    const previewContainer = document.querySelector('.author-microblog__post-photo-preview');
    const previewImage = document.getElementById('postPhotoImage');
    const removeBtn = document.getElementById('removePhotoBtn');

    function resetPhoto() {
        fileInput.value = "";
        previewImage.src = "";
        previewContainer.style.display = "none";
    }

    fileInput.addEventListener('change', (event) => {
        const file = event.target.files[0];

        if (file) {
            const maxLength = 20;
            let fileName = file.name;

            previewImage.src = URL.createObjectURL(file);
            previewContainer.style.display = "block";

            if (fileName.length > maxLength) {
                const extension = fileName.split('.').pop();
                const nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                fileName = nameWithoutExt.substring(0, maxLength - 5) + '...' + extension;
            }

            fileNameText.textContent = fileName;
        } else {
            fileNameText.textContent = defaultText;
            previewImage.src = "";
            previewContainer.style.display = "none";
            resetPhoto();
        }
    });

    removeBtn.addEventListener("click", (e) => {
        e.preventDefault();
        resetPhoto();
    });
});