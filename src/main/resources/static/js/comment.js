document.addEventListener('click', (e) => {
    const replyBtn = e.target.closest('.comment__reply-btn');
    if (replyBtn) {
        const commentBody = replyBtn.closest('.comment__body');
        const replyForm = commentBody.querySelector(':scope > .comment__reply-form');

        if (replyForm) {
            const editForm = commentBody.querySelector(':scope > .comment__edit-form');
            if (editForm?.classList.contains('is-open')) {
                toggleEditForm(commentBody, editForm, false);
            }

            const isOpen = replyForm.classList.toggle('is-open');
            if (isOpen) {
                replyForm.querySelector('.comment__reply-input')?.focus();
            }
        }
        return;
    }

    const editBtn = e.target.closest('.comment__edit-btn');
    if (editBtn) {
        const commentBody = editBtn.closest('.comment__body');
        const editForm = commentBody.querySelector(':scope > .comment__edit-form');

        if (editForm) {
            const replyForm = commentBody.querySelector(':scope > .comment__reply-form');
            if (replyForm?.classList.contains('is-open')) {
                replyForm.classList.remove('is-open');
            }

            const isOpen = !editForm.classList.contains('is-open');
            toggleEditForm(commentBody, editForm, isOpen);
        }
    }
});

function toggleEditForm(commentBody, editForm, show) {
    const textElements = commentBody.querySelectorAll(':scope > .comment__text');
    let textElement = null;
    textElements.forEach(el => {
        if (!el.textContent.includes('[комментарий удалён]')) {
            textElement = el;
        }
    });

    if (show) {
        editForm.classList.add('is-open');
        if (textElement) textElement.style.display = 'none';

        const input = editForm.querySelector('.comment__edit-input');
        if (input) {
            input.focus();
            input.selectionStart = input.selectionEnd = input.value.length;
        }
    } else {
        editForm.classList.remove('is-open');
        if (textElement) textElement.style.display = '';
    }
}