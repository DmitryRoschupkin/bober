document.addEventListener('click', (e) => {
    const replyBtn = e.target.closest('.comment__reply-btn');
    if (replyBtn) {
        const commentBody = replyBtn.closest('.comment__body, .post-comment__body');
        if (!commentBody) return;

        const replyForm = commentBody.querySelector('.comment__reply-form');
        if (replyForm) {
            const editForm = commentBody.querySelector('.comment__edit-form');
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
        const commentBody = editBtn.closest('.comment__body, .post-comment__body');
        if (!commentBody) return;

        const editForm = commentBody.querySelector('.comment__edit-form');
        if (editForm) {
            const replyForm = commentBody.querySelector('.comment__reply-form');
            if (replyForm?.classList.contains('is-open')) {
                replyForm.classList.remove('is-open');
            }

            const isOpen = !editForm.classList.contains('is-open');
            toggleEditForm(commentBody, editForm, isOpen);
        }
    }
});

function toggleEditForm(commentBody, editForm, show) {
    const textElements = commentBody.querySelectorAll('.comment__text');
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

document.addEventListener('DOMContentLoaded', () => {
    const formatter = new Intl.DateTimeFormat('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone
    });

    document.querySelectorAll('time[datetime]').forEach(timeEl => {
        let isoString = timeEl.getAttribute('datetime');
        if (!isoString) return;

        if (!isoString.endsWith('Z') && !isoString.includes('+') && !isoString.includes('-')) {
            isoString += 'Z';
        }

        const date = new Date(isoString);
        if (!isNaN(date.getTime())) {
            const formattedDate = formatter.format(date);

            if (timeEl.classList.contains('author-microblog__post-updated')
                || timeEl.classList.contains('post-comment__edited')
                || timeEl.classList.contains('comment__edited')) {
                timeEl.setAttribute('title', formattedDate);
            } else {
                timeEl.textContent = formattedDate;
            }
        }
    });
});